package com.veterinaria.veterinariaapp.ui;

import com.veterinaria.veterinariaapp.model.Cita;
import com.veterinaria.veterinariaapp.model.Mascota;
import com.veterinaria.veterinariaapp.repository.CitaRepository;
import com.veterinaria.veterinariaapp.repository.MascotaRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Importante: Necesitarás la librería JDateChooser. Añade esto a tu pom.xml si aún no está:
// <dependency>
//     <groupId>com.toedter</groupId>
//     <artifactId>jcalendar</artifactId>
//     <version>1.4</version>
// </dependency>
import com.toedter.calendar.JDateChooser;
import java.time.format.DateTimeFormatter;


public class CitasViewForm extends JFrame {

    // --- Repositorios y Modelos de la Vista ---
    private final CitaRepository citaRepository = new CitaRepository();
    private final MascotaRepository mascotaRepository = new MascotaRepository(); // Para el ComboBox
    private final CitaTableModel tableModel = new CitaTableModel(new ArrayList<>());

    // --- Componentes de la UI ---
    private JTable tablaCitas;
    private JButton btnAgendar;

    public CitasViewForm() {
        setTitle("Gestión de Citas");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(1024, 768); // Un poco más grande para las columnas de citas
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

        var cols = tablaCitas.getColumnModel();
        if (cols.getColumnCount() > CitaTableModel.COL_ACCIONES) {
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
            List<Cita> lista = citaRepository.listar();
            tableModel.setData(lista);
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
            // Llenamos el ComboBox con las mascotas existentes
            mascotaRepository.listar().forEach(cmbMascotas::addItem);
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
        // Placeholder para la hora (se puede mejorar con un JSpinner)
        JTextField txtHora = new JTextField("10:00", 5);
        JTextArea txtMotivo = new JTextArea(4, 25);
        txtMotivo.setLineWrap(true);
        txtMotivo.setWrapStyleWord(true);
        JScrollPane scrollMotivo = new JScrollPane(txtMotivo);

        // --- Layout del Formulario ---
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

        // --- Lógica de los Botones ---
        btnCancelar.addActionListener(e -> dialogo.dispose());
        btnGuardar.addActionListener(e -> {
            try {
                // 1. Recoger datos del formulario
                Mascota mascotaSeleccionada = (Mascota) cmbMascotas.getSelectedItem();
                if (mascotaSeleccionada == null) {
                    JOptionPane.showMessageDialog(dialogo, "Debe seleccionar una mascota.", "Validación", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                if (dateChooser.getDate() == null) {
                    JOptionPane.showMessageDialog(dialogo, "Debe seleccionar una fecha.", "Validación", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                String motivo = txtMotivo.getText().trim();
                if (motivo.isEmpty()) {
                    JOptionPane.showMessageDialog(dialogo, "El motivo no puede estar vacío.", "Validación", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Combinar fecha y hora
                java.util.Date fecha = dateChooser.getDate();
                String[] horaMin = txtHora.getText().split(":");
                int hora = Integer.parseInt(horaMin[0]);
                int min = Integer.parseInt(horaMin[1]);
                
                LocalDateTime fechaHora = new java.sql.Timestamp(fecha.getTime()).toLocalDateTime()
                                            .withHour(hora).withMinute(min);
                
                
                                // 2.A. Validar que la cita no sea en el pasado
                if (fechaHora.isBefore(LocalDateTime.now())) {
                    JOptionPane.showMessageDialog(dialogo, "No se puede agendar una cita en una fecha u hora pasada.", "Validación", JOptionPane.ERROR_MESSAGE);
                    return; // Detiene el proceso
                }

                // 2.B. Validar que no haya otra cita a la misma hora
                if (citaRepository.existeCitaConflictiva(fechaHora, null)) {
                    JOptionPane.showMessageDialog(dialogo, "Ya existe una cita programada para esa fecha y hora.", "Conflicto de Horario", JOptionPane.ERROR_MESSAGE);
                    return; // Detiene el proceso
                }

                // 2. Crear el objeto Cita
                Cita nuevaCita = new Cita();
                nuevaCita.setMascotaId(mascotaSeleccionada.getId());
                nuevaCita.setFechaHora(fechaHora);
                nuevaCita.setMotivo(motivo);

                // 3. Llamar al repositorio para guardar
                citaRepository.insertar(nuevaCita);

                // 4. Actualizar la UI
                recargarTabla();
                JOptionPane.showMessageDialog(this, "Cita agendada correctamente.");
                dialogo.dispose();

            } catch (Exception ex) {
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
    
// --- Lógica para el diálogo de editar (muy similar al de agendar) ---
    private void mostrarDialogoEditar(Cita cita) {
        // --- Creamos un diálogo para el formulario ---
        JDialog dialogo = new JDialog(this, "Editar Cita #" + cita.getId(), true);
        dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // --- Componentes del Formulario ---
        JComboBox<Mascota> cmbMascotas = new JComboBox<>();
        try {
            mascotaRepository.listar().forEach(cmbMascotas::addItem);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialogo, "No se pudieron cargar las mascotas.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        // Personalizamos el renderizado del ComboBox (igual que en agendar)
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

        // ===================================================================
        //  PUNTO CLAVE 1: PRE-CARGAR LOS DATOS DE LA CITA EXISTENTE
        // ===================================================================
        dateChooser.setDate(java.sql.Timestamp.valueOf(cita.getFechaHora()));
        txtHora.setText(cita.getFechaHora().format(DateTimeFormatter.ofPattern("HH:mm")));
        txtMotivo.setText(cita.getMotivo());

        // Para seleccionar la mascota correcta en el ComboBox, necesitamos buscarla
        for (int i = 0; i < cmbMascotas.getItemCount(); i++) {
            Mascota mascotaEnCombo = cmbMascotas.getItemAt(i);
            if (mascotaEnCombo.getId().equals(cita.getMascotaId())) {
                cmbMascotas.setSelectedIndex(i);
                break;
            }
        }
        // ===================================================================

        // --- Layout del Formulario (idéntico al de agendar) ---
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
        JButton btnGuardar = new JButton("Guardar Cambios"); // Texto del botón cambiado
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);

        fila++; gbc.gridx = 0; gbc.gridy = fila; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0;
        panelContenido.add(panelBotones, gbc);

        // --- Lógica de los Botones ---
        btnCancelar.addActionListener(e -> dialogo.dispose());
        btnGuardar.addActionListener(e -> {
            try {
                // 1. Recoger datos del formulario (igual que en agendar)
                Mascota mascotaSeleccionada = (Mascota) cmbMascotas.getSelectedItem();
                java.util.Date fecha = dateChooser.getDate();
                String motivo = txtMotivo.getText().trim();
                
                // ... (aquí irían las mismas validaciones de campos vacíos) ...
                
                String[] horaMin = txtHora.getText().split(":");
                LocalDateTime fechaHora = new java.sql.Timestamp(fecha.getTime()).toLocalDateTime()
                                            .withHour(Integer.parseInt(horaMin[0]))
                                            .withMinute(Integer.parseInt(horaMin[1]));
                
                
                // 2.A. Validar que la cita no sea en el pasado
                if (fechaHora.isBefore(LocalDateTime.now())) {
                    JOptionPane.showMessageDialog(dialogo, "No se puede agendar una cita en una fecha u hora pasada.", "Validación", JOptionPane.ERROR_MESSAGE);
                    return; // Detiene el proceso
                }

                // 2.B. Validar que no haya otra cita a la misma hora (excluyendo la actual)
                if (citaRepository.existeCitaConflictiva(fechaHora, cita.getId())) {
                    JOptionPane.showMessageDialog(dialogo, "Ya existe otra cita programada para esa fecha y hora.", "Conflicto de Horario", JOptionPane.ERROR_MESSAGE);
                    return; // Detiene el proceso
                }

                // ===================================================================
                //  PUNTO CLAVE 2: ACTUALIZAR EL OBJETO CITA Y LLAMAR A `actualizar`
                // ===================================================================
                cita.setMascotaId(mascotaSeleccionada.getId());
                cita.setFechaHora(fechaHora);
                cita.setMotivo(motivo);
                // El estado se podría cambiar aquí si tuvieras un ComboBox para ello.
                
                citaRepository.actualizar(cita); // <--- Llamamos al método de actualizar
                // ===================================================================

                // 4. Actualizar la UI
                recargarTabla();
                JOptionPane.showMessageDialog(this, "Cita actualizada correctamente.");
                dialogo.dispose();

            } catch (Exception ex) {
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
    //  (VERSIÓN FINAL CORREGIDA)
    // =================================================================
    
    // RENDERER (DIBUJO)
    private static class AccionesRenderer extends JPanel implements TableCellRenderer {
        private final JButton btnEdit = new JButton("Editar");
        private final JButton btnDel = new JButton("Cancelar");

        AccionesRenderer() {
            setOpaque(true);
            // SOLUCIÓN: Usar GridLayout para forzar tamaños iguales y asegurar visibilidad.
            setLayout(new GridLayout(1, 2, 6, 0)); // 1 fila, 2 columnas, 6px de gap H, 0px de gap V
            
            btnEdit.setFocusable(false);
            btnDel.setFocusable(false);
            
            add(btnEdit);
            add(btnDel);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            // Se ajusta el color de fondo de los botones al de la selección de la tabla
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
        private final JPanel panel = new JPanel(); // El layout se define abajo
        private final JButton btnEdit = new JButton("Editar");
        private final JButton btnDel = new JButton("Cancelar"); // Texto simplificado para consistencia
        private int editingRow = -1;

        AccionesEditor() {
            // SOLUCIÓN: Usar GridLayout aquí también.
            panel.setLayout(new GridLayout(1, 2, 6, 0));
            panel.add(btnEdit);
            panel.add(btnDel);

            // CORRECCIÓN: Detener edición ANTES de mostrar el diálogo.
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
                    citaRepository.eliminar(c.getId());
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
            // Se ajusta el color de fondo de los botones al de la selección de la tabla
            panel.setBackground(table.getSelectionBackground());
            for(Component c : panel.getComponents()){
                c.setBackground(table.getSelectionBackground());
            }
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }

    // --- Main para pruebas (opcional) ---
    public static void main(String[] args) {
        // Para que la UI se vea como el sistema operativo nativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new CitasViewForm().setVisible(true));
    }
}