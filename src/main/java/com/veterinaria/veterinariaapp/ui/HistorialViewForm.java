package com.veterinaria.veterinariaapp.ui;

import com.veterinaria.veterinariaapp.model.HistorialMedico;
import com.veterinaria.veterinariaapp.model.Mascota;
import com.veterinaria.veterinariaapp.service.HistorialService;
import com.veterinaria.veterinariaapp.service.MascotaService;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HistorialViewForm extends JFrame {

    private final HistorialService historialService;
    private final MascotaService mascotaService;

    private JComboBox<MascotaItem> cboMascotas;
    private HistorialTableModel tableModel;
    private JTable tablaHistorial;
    private JTextArea txtDescripcion;
    private JTextField txtTratamiento;
    private JButton btnGuardar;

    public HistorialViewForm(HistorialService hs, MascotaService ms) {
        this.historialService = hs;
        this.mascotaService = ms;
        initUI();
    }

    private void initUI() {
        setTitle("Historial Clínico");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(950, 600);
        setLocationRelativeTo(null);

        // 1. Panel Superior (Selección)
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        top.setBorder(BorderFactory.createMatteBorder(0,0,1,0, Color.LIGHT_GRAY));
        
        cboMascotas = new JComboBox<>();
        cboMascotas.setPreferredSize(new Dimension(350, 30));
        JButton btnCargar = new JButton("Ver Historial");
        
        top.add(new JLabel("Seleccionar Paciente (Mascota):"));
        top.add(cboMascotas);
        top.add(btnCargar);

        // 2. Centro (Tabla)
        tableModel = new HistorialTableModel(new ArrayList<>());
        tablaHistorial = new JTable(tableModel);
        tablaHistorial.setRowHeight(28);
        tablaHistorial.getColumnModel().getColumn(0).setPreferredWidth(130);
        tablaHistorial.getColumnModel().getColumn(1).setPreferredWidth(450);
        
        JScrollPane scroll = new JScrollPane(tablaHistorial);
        scroll.setBorder(BorderFactory.createTitledBorder("Antecedentes"));

        // 3. Abajo (Formulario Nuevo)
        JPanel bot = new JPanel(new GridBagLayout());
        bot.setBorder(BorderFactory.createTitledBorder("Nueva Consulta"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6,6,6,6);
        g.fill = GridBagConstraints.HORIZONTAL;

        txtDescripcion = new JTextArea(3, 40);
        txtDescripcion.setLineWrap(true);
        txtTratamiento = new JTextField(20);
        btnGuardar = new JButton("Guardar Diagnóstico");
        btnGuardar.setBackground(new Color(220, 240, 255));

        g.gridx=0; g.gridy=0; bot.add(new JLabel("Diagnóstico:"), g);
        g.gridx=1; g.gridy=0; g.weightx=1.0; bot.add(new JScrollPane(txtDescripcion), g);
        
        g.gridx=0; g.gridy=1; g.weightx=0; bot.add(new JLabel("Tratamiento:"), g);
        g.gridx=1; g.gridy=1; g.weightx=1.0; bot.add(txtTratamiento, g);

        g.gridx=2; g.gridy=0; g.gridheight=2; g.weightx=0; g.fill=GridBagConstraints.VERTICAL;
        bot.add(btnGuardar, g);

        // Armar ventana
        JPanel main = new JPanel(new BorderLayout(10,10));
        main.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        main.add(top, BorderLayout.NORTH);
        main.add(scroll, BorderLayout.CENTER);
        main.add(bot, BorderLayout.SOUTH);
        setContentPane(main);

        // Eventos
        btnCargar.addActionListener(e -> cargarHistorial());
        cboMascotas.addActionListener(e -> cargarHistorial());
        btnGuardar.addActionListener(e -> onGuardar());
    }

    public void recargarDatos() {
        cboMascotas.removeAllItems();
        try {
            List<Mascota> lista = mascotaService.listarMascotas();
            for(Mascota m : lista) cboMascotas.addItem(new MascotaItem(m));
            tableModel.setData(new ArrayList<>()); // Limpiar tabla al recargar
        } catch(Exception e) { e.printStackTrace(); }
    }

    private void cargarHistorial() {
        MascotaItem item = (MascotaItem) cboMascotas.getSelectedItem();
        if(item == null) return;
        try {
            var data = historialService.listarPorMascota(item.m.getId());
            tableModel.setData(data);
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void onGuardar() {
        MascotaItem item = (MascotaItem) cboMascotas.getSelectedItem();
        if(item == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una mascota.");
            return;
        }
        try {
            HistorialMedico h = new HistorialMedico(
                item.m.getId(), LocalDateTime.now(), 
                txtDescripcion.getText(), txtTratamiento.getText()
            );
            historialService.registrarEntrada(h);
            txtDescripcion.setText(""); txtTratamiento.setText("");
            cargarHistorial();
            JOptionPane.showMessageDialog(this, "Guardado.");
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helper Combo
    private record MascotaItem(Mascota m) {
        @Override public String toString() { return m.getNombre() + " (" + m.getClienteNombre() + ")"; }
    }
}