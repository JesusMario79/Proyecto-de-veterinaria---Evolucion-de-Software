package com.veterinaria.veterinariaapp.ui;

import com.veterinaria.veterinariaapp.model.Pago;
import com.veterinaria.veterinariaapp.service.PagoService;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class PagoViewForm extends JFrame {

    private final PagoService service;
    private final PagoTableModel tableModel = new PagoTableModel();
    
    private JTextField txtCitaId;
    private JTextField txtMonto;
    private JComboBox<String> cmbMetodo;
    private JTable tabla;

    // Inyección de Dependencia del Servicio
    public PagoViewForm(PagoService service) {
        this.service = service;
        setTitle("Gestión de Pagos");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        initUI();
        cargarDatos();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        // Panel de Formulario
        JPanel panelForm = new JPanel(new GridLayout(4, 2, 5, 5));
        panelForm.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        panelForm.add(new JLabel("ID Cita:"));
        txtCitaId = new JTextField();
        panelForm.add(txtCitaId);

        panelForm.add(new JLabel("Monto:"));
        txtMonto = new JTextField();
        panelForm.add(txtMonto);

        panelForm.add(new JLabel("Método de Pago:"));
        cmbMetodo = new JComboBox<>(new String[]{"Efectivo", "Tarjeta", "Yape", "Plin"});
        panelForm.add(cmbMetodo);

        JButton btnGuardar = new JButton("Registrar Pago");
        btnGuardar.addActionListener(e -> guardarPago());
        panelForm.add(new JLabel("")); // Espacio vacío
        panelForm.add(btnGuardar);

        add(panelForm, BorderLayout.NORTH);

        // Panel de Tabla
        tabla = new JTable(tableModel);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    private void cargarDatos() {
        try {
            List<Pago> pagos = service.listarPagos();
            tableModel.setDatos(pagos);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar: " + e.getMessage());
        }
    }

    private void guardarPago() {
        try {
            // Recoger datos
            String idCitaStr = txtCitaId.getText().trim();
            String montoStr = txtMonto.getText().trim();
            String metodo = (String) cmbMetodo.getSelectedItem();

            // Validaciones básicas de formato antes de llamar al servicio
            if (idCitaStr.isEmpty() || montoStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Complete todos los campos");
                return;
            }

            Integer citaId = Integer.parseInt(idCitaStr);
            BigDecimal monto = new BigDecimal(montoStr);

            // Crear objeto
            Pago nuevoPago = new Pago(citaId, monto, metodo);

            // Llamar al servicio (Lógica de negocio)
            service.registrarPago(nuevoPago);

            // Limpiar y recargar
            txtCitaId.setText("");
            txtMonto.setText("");
            cargarDatos();
            JOptionPane.showMessageDialog(this, "Pago registrado con éxito");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El ID de cita y el Monto deben ser números válidos.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}