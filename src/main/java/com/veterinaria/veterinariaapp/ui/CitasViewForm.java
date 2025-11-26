package com.veterinaria.veterinariaapp.ui;

import com.veterinaria.veterinariaapp.model.Cita;
import com.veterinaria.veterinariaapp.model.Mascota;
// --- ¡CAMBIOS EN IMPORTS! ---
// YA NO IMPORTA REPOSITORIOS (excepto para el 'main' de prueba)
import com.veterinaria.veterinariaapp.repository.CitaRepository; 
import com.veterinaria.veterinariaapp.repository.IMascotaRepository;
import com.veterinaria.veterinariaapp.repository.ICitaRepository;
import com.veterinaria.veterinariaapp.repository.MascotaRepository;
import com.veterinaria.veterinariaapp.service.CitaService; // <-- IMPORTA EL SERVICIO

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.toedter.calendar.JDateChooser;
import java.time.format.DateTimeFormatter;


public class CitasViewForm extends JFrame {

    // --- ¡CORREGIDO! ---
    // Ya no crea repositorios. Ahora depende de CitaService
    private final CitaService citaService; // <-- ¡Depende del Servicio!
    private final CitaTableModel tableModel = new CitaTableModel(new ArrayList<>());

    // --- Componentes de la UI ---
    private JTable tablaCitas;
    private JButton btnAgendar;

    // --- ¡CORREGIDO! ---
    // Recibe el servicio por inyección de dependencia
    public CitasViewForm(CitaService citaService) {
        this.citaService = citaService; // <-- Lo recibe aquí

        setTitle("Gestión de Citas");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null);

        initUI();
        configurarTabla();
        recargarTabla();
    }

    private void initUI() {
        // --- Panel Superior (Título y Botón) ---
        JLabel lblTitulo = new JLabel("Agenda de Citas");
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 22f));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        btnAgendar = new JButton("Agendar Nueva Cita");
        btnAgendar.addActionListener(e -> mostrarDialogoAgendar());

        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.add(lblTitulo, BorderLayout.WEST);
        JPanel panelEste = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelEste.add(btnAgendar);
        panelNorte.add(panelEste, BorderLayout.EAST);

        // --- Tabla Principal ---
        tablaCitas = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tablaCitas);

        // --- Panel Raíz ---
        JPanel panelRaiz = new JPanel(new BorderLayout(8, 8));
        panelRaiz.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        panelRaiz.add(panelNorte, BorderLayout.NORTH);
        panelRaiz.add(scrollPane, BorderLayout.CENTER);

        setContentPane(panelRaiz);
    }

    private void configurarTabla() {
        tablaCitas.setRowHeight(36);
        tablaCitas.setFillsViewportHeight(true);
        tablaCitas.setAutoCreateRowSorter(true);
        tablaCitas.getTableHeader().setReorderingAllowed(false);
        tablaCitas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Centrar texto en columnas específicas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        // Esperamos que el TableModel esté listo
        // (Este chequeo previene errores si el modelo se carga vacío)
        if (tablaCitas.getColumnModel().getColumnCount() > CitaTableModel.COL_ACCIONES) {
            var cols = tablaCitas.getColumnModel();
            cols.getColumn(0).setPreferredWidth(40); // ID
            cols.getColumn(1).setPreferredWidth(150); // Fecha y Hora
            cols.getColumn(5).setPreferredWidth(100); // Estado
            
            cols.getColumn(0).setCellRenderer(centerRenderer);
            cols.getColumn(1).setCellRenderer(centerRenderer);
            cols.getColumn(5).setCellRenderer(centerRenderer);

            // Configurar la columna de acciones con botones
            int colAcciones = tablaCitas.getColumnModel().getColumnIndex("Acciones");
            var accionesCol = tablaCitas.getColumnModel().getColumn(colAcciones);
            accionesCol.setMinWidth(190);
            accionesCol.setMaxWidth(220);
            accionesCol.setCellRenderer(new AccionesRenderer());
            accionesCol.setCellEditor(new AccionesEditor());
        }
    }

    public void recargarTabla() {
        try {
            // --- ¡CORREGIDO! ---
            List<Cita> lista = citaService.listarCitas(); // Llama al servicio
            tableModel.setData(lista);
            
            // Re-configuramos la tabla por si era la primera carga
            configurarTabla(); 
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar las citas: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarDialogoAgendar() {
        // --- Creamos un diálogo para el formulario ---
        JDialog dialogo = new JDialog(this, "Agendar Nueva Cita", true);
        dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // --- Componentes del Formulario ---
        JComboBox<Mascota> cmbMascotas = new JComboBox<>();
        try {
            // --- ¡CORREGIDO! ---
            citaService.listarMascotas().forEach(cmbMascotas::addItem); // Llama al servicio
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialogo, "No se pudieron cargar las mascotas.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        // Personalizamos cómo se muestra el objeto Mascota en el ComboBox
        cmbMascotas.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Mascota) {
                    Mascota m = (Mascota) value;
                    setText(m.getNombre() + " (" + m.getClienteNombre() + ")");
                }
                return this;
            }
        });


        JDateChooser dateChooser = new JDateChooser();
        JTextField txtHora = new JTextField("10:00", 5);
        JTextArea txtMotivo = new JTextArea(4, 25);
        txtMotivo.setLineWrap(true);
        txtMotivo.setWrapStyleWord(true);
        JScrollPane scrollMotivo = new JScrollPane(txtMotivo);

        // --- Layout del Formulario (No cambia) ---
        JPanel panelContenido = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int fila = 0;
        gbc.gridx = 0; gbc.gridy = fila; panelContenido.add(new JLabel("Mascota:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; panelContenido.add(cmbMascotas, gbc);
        
        fila++; gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0; panelContenido.add(new JLabel("Fecha:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; panelContenido.add(dateChooser, gbc);
        
        fila++; gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0; panelContenido.add(new JLabel("Hora (HH:mm):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; panelContenido.add(txtHora, gbc);
        
        fila++; gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0; gbc.anchor = GridBagConstraints.NORTH; panelContenido.add(new JLabel("Motivo:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1; panelContenido.add(scrollMotivo, gbc);


        // --- Botones de Acción ---
        JButton btnCancelar = new JButton("Cancelar");
        JButton btnGuardar = new JButton("Guardar Cita");
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);

        fila++; gbc.gridx = 0; gbc.gridy = fila; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0;
        panelContenido.add(panelBotones, gbc);

        // --- Lógica de los Botones (¡CORREGIDA!) ---
        btnCancelar.addActionListener(e -> dialogo.dispose());
        btnGuardar.addActionListener(e -> {
            try {
                // 1. Recoger datos del formulario
                Mascota mascotaSeleccionada = (Mascota) cmbMascotas.getSelectedItem();
                java.util.Date fecha = dateChooser.getDate();
                String motivo = txtMotivo.getText().trim();

                // 2. Validaciones MÍNIMAS de la vista (solo para UI)
                if (mascotaSeleccionada == null) {
                    JOptionPane.showMessageDialog(dialogo, "Debe seleccionar una mascota.", "Validación", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (fecha == null) {
                    JOptionPane.showMessageDialog(dialogo, "Debe seleccionar una fecha.", "Validación", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (motivo.isEmpty()) {
                     JOptionPane.showMessageDialog(dialogo, "El motivo no puede estar vacío.", "Validación", JOptionPane.WARNING_MESSAGE);
                     return;
                }

                // 3. Combinar fecha y hora
                String[] horaMin = txtHora.getText().split(":");
                int hora = Integer.parseInt(horaMin[0]);
                int min = Integer.parseInt(horaMin[1]);
                LocalDateTime fechaHora = new java.sql.Timestamp(fecha.getTime()).toLocalDateTime()
                        .withHour(hora).withMinute(min);

                // 4. Crear el objeto Cita (simple POJO)
                Cita nuevaCita = new Cita();
                nuevaCita.setMascotaId(mascotaSeleccionada.getId());
                nuevaCita.setFechaHora(fechaHora);
                nuevaCita.setMotivo(motivo);

                // 5. Delegar TODA la lógica de negocio al servicio
                //    ¡YA NO HAY IFs DE LÓGICA DE NEGOCIO AQUÍ!
                citaService.agendarCita(nuevaCita); // <-- ¡SRP y DIP en acción!

                // 6. Actualizar la UI si todo salió bien
                recargarTabla();
                JOptionPane.showMessageDialog(this, "Cita agendada correctamente.");
                dialogo.dispose();

            } catch (IllegalArgumentException ex) { // Captura errores de NEGOCIO
                JOptionPane.showMessageDialog(dialogo, ex.getMessage(), "Error de Validación", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) { // Captura errores genéricos (BD, formato de hora, etc)
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialogo, "Error al guardar la cita: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialogo.setContentPane(panelContenido);
        dialogo.pack();
        dialogo.setSize(500, 400); // Tamaño fijo para el diálogo
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }
    
    // --- Lógica para el diálogo de editar (¡CORREGIDA!) ---
    private void mostrarDialogoEditar(Cita cita) {
        JDialog dialogo = new JDialog(this, "Editar Cita #" + cita.getId(), true);
        dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // --- Componentes del Formulario ---
        JComboBox<Mascota> cmbMascotas = new JComboBox<>();
        try {
            // --- ¡CORREGIDO! ---
            citaService.listarMascotas().forEach(cmbMascotas::addItem);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialogo, "No se pudieron cargar las mascotas.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        cmbMascotas.setRenderer(new DefaultListCellRenderer() {
             @Override
             public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                 super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                 if (value instanceof Mascota) {
                     Mascota m = (Mascota) value;
                     setText(m.getNombre() + " (" + m.getClienteNombre() + ")");
                 }
                 return this;
             }
         });
        
        JDateChooser dateChooser = new JDateChooser();
        JTextField txtHora = new JTextField(5);
        JTextArea txtMotivo = new JTextArea(4, 25);
        txtMotivo.setLineWrap(true);
        txtMotivo.setWrapStyleWord(true);
        JScrollPane scrollMotivo = new JScrollPane(txtMotivo);
        
        // --- PRE-CARGAR DATOS (No cambia) ---
        dateChooser.setDate(java.sql.Timestamp.valueOf(cita.getFechaHora()));
        txtHora.setText(cita.getFechaHora().format(DateTimeFormatter.ofPattern("HH:mm")));
        txtMotivo.setText(cita.getMotivo());
        
        // Esta lógica ahora funciona gracias al fix en CitaRepository (mascota_id)
        for (int i = 0; i < cmbMascotas.getItemCount(); i++) {
            Mascota mascotaEnCombo = cmbMascotas.getItemAt(i);
            if (mascotaEnCombo.getId().equals(cita.getMascotaId())) {
                cmbMascotas.setSelectedIndex(i);
                break;
            }
        }
        
        // --- Layout del Formulario (No cambia) ---
        JPanel panelContenido = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int fila = 0;
        gbc.gridx = 0; gbc.gridy = fila; panelContenido.add(new JLabel("Mascota:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; panelContenido.add(cmbMascotas, gbc);
        
        fila++; gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0; panelContenido.add(new JLabel("Fecha:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; panelContenido.add(dateChooser, gbc);
        
        fila++; gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0; panelContenido.add(new JLabel("Hora (HH:mm):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; panelContenido.add(txtHora, gbc);
        
        fila++; gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0; gbc.anchor = GridBagConstraints.NORTH; panelContenido.add(new JLabel("Motivo:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1; panelContenido.add(scrollMotivo, gbc);


        // --- Botones de Acción ---
        JButton btnCancelar = new JButton("Cancelar");
        JButton btnGuardar = new JButton("Guardar Cambios");
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.add(btnCancelar); 
        panelBotones.add(btnGuardar);
        
        fila++; gbc.gridx = 0; gbc.gridy = fila; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0;
        panelContenido.add(panelBotones, gbc);


        // --- Lógica de los Botones (¡CORREGIDA!) ---
        btnCancelar.addActionListener(e -> dialogo.dispose());
        btnGuardar.addActionListener(e -> {
            try {
                // 1. Recoger datos del formulario
                Mascota mascotaSeleccionada = (Mascota) cmbMascotas.getSelectedItem();
                java.util.Date fecha = dateChooser.getDate();
                String motivo = txtMotivo.getText().trim();
                // ... (Validaciones de UI de campos vacíos...) ...
                
                // 2. Combinar fecha y hora
                String[] horaMin = txtHora.getText().split(":");
                LocalDateTime fechaHora = new java.sql.Timestamp(fecha.getTime()).toLocalDateTime()
                        .withHour(Integer.parseInt(horaMin[0]))
                        .withMinute(Integer.parseInt(horaMin[1]));
                
                // 3. Actualizar el objeto CITA existente
                cita.setMascotaId(mascotaSeleccionada.getId());
                cita.setFechaHora(fechaHora);
                cita.setMotivo(motivo);
                
                // 4. Delegar TODA la lógica de negocio al servicio
                citaService.actualizarCita(cita); // <-- ¡SRP y DIP en acción!

                // 5. Actualizar la UI
                recargarTabla();
                JOptionPane.showMessageDialog(this, "Cita actualizada correctamente.");
                dialogo.dispose();
                
            } catch (IllegalArgumentException ex) { // Captura errores de NEGOCIO
                JOptionPane.showMessageDialog(dialogo, ex.getMessage(), "Error de Validación", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) { // Captura errores genéricos
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialogo, "Error al actualizar la cita: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialogo.setContentPane(panelContenido);
        dialogo.pack();
        dialogo.setSize(500, 400); 
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }

    // =================================================================
    //  CLASES INTERNAS PARA LOS BOTONES DE ACCIONES EN LA TABLA
    // =================================================================
    
    // RENDERER (DIBUJO)
    private static class AccionesRenderer extends JPanel implements TableCellRenderer {
        private final JButton btnEdit = new JButton("Editar");
        private final JButton btnDel = new JButton("Cancelar");
        AccionesRenderer() {
             setOpaque(true); 
             setLayout(new GridLayout(1, 2, 6, 0));
             btnEdit.setFocusable(false); 
             btnDel.setFocusable(false);
             add(btnEdit); 
             add(btnDel);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Color bg = isSelected ? table.getSelectionBackground() : table.getBackground();
            setBackground(bg);
            for(Component c : getComponents()){ 
                c.setBackground(bg); 
            }
            return this;
        }
    }

    // EDITOR (LÓGICA REAL)
    private class AccionesEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel = new JPanel();
        private final JButton btnEdit = new JButton("Editar");
        private final JButton btnDel = new JButton("Cancelar");
        private int editingRow = -1;

        AccionesEditor() {
            panel.setLayout(new GridLayout(1, 2, 6, 0));
            panel.add(btnEdit);
            panel.add(btnDel);

            btnEdit.addActionListener(e -> { 
                fireEditingStopped(); 
                onEditar(); 
            });
            btnDel.addActionListener(e -> { 
                fireEditingStopped(); 
                onCancelar(); 
            });
        }

        private void onEditar() {
            if (editingRow < 0) return;
            int modelRow = tablaCitas.convertRowIndexToModel(editingRow);
            Cita c = tableModel.getAt(modelRow);
            // (Gracias al fix, c.getMascotaId() ahora tiene un valor)
            mostrarDialogoEditar(c);
        }

        private void onCancelar() {
            if (editingRow < 0) return;
            int modelRow = tablaCitas.convertRowIndexToModel(editingRow);
            Cita c = tableModel.getAt(modelRow);

            int ok = JOptionPane.showConfirmDialog(
                    CitasViewForm.this,
                    "¿Está seguro de que desea cancelar la cita para \"" + c.getMascotaNombre() + "\"?",
                    "Confirmar cancelación",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (ok == JOptionPane.YES_OPTION) {
                try {
                    // --- ¡CORREGIDO! ---
                    citaService.cancelarCita(c.getId()); // Llama al servicio
                    recargarTabla();
                    JOptionPane.showMessageDialog(CitasViewForm.this, "Cita cancelada.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(CitasViewForm.this, "Error al cancelar la cita: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            editingRow = row;
            panel.setBackground(table.getSelectionBackground());
            for(Component c : panel.getComponents()){ 
                c.setBackground(table.getSelectionBackground()); 
            }
            return panel;
        }

        @Override
        public Object getCellEditorValue() { return null; }
    }

    // --- Main para pruebas (¡CORREGIDO!) ---
    // Este método 'main' te muestra CÓMO se deben ensamblar
    // las piezas. Este es el "plano" que debes seguir en tu MainWindow.
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // --- ¡EL ENSAMBLAJE SUCEDE AQUÍ! ---
        SwingUtilities.invokeLater(() -> {
            // 1. Crear implementaciones concretas
            ICitaRepository citaRepo = new CitaRepository();
            IMascotaRepository mascotaRepo = new MascotaRepository();
            
            // 2. Crear el servicio e inyectar
            CitaService servicio = new CitaService(citaRepo, mascotaRepo);
            
            // 3. Crear la vista e inyectar
            new CitasViewForm(servicio).setVisible(true);
        });
    }
}