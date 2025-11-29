package com.veterinaria.veterinariaapp.ui;

import com.veterinaria.veterinariaapp.model.Servicios;
import com.veterinaria.veterinariaapp.service.ServiciosService; 
import com.veterinaria.veterinariaapp.repository.IServiciosRepository; 
import com.veterinaria.veterinariaapp.repository.ServicioRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ServiciosViewForm extends JFrame {

    private final ServiciosService serviciosService; 
    private final ServiciosTableModel model = new ServiciosTableModel(new ArrayList<>()); 

    private JTable tabla;
    private JButton btnAgregar;

    public ServiciosViewForm(ServiciosService serviciosService) {
        this.serviciosService = serviciosService; 

        setTitle("Gestión de Servicios");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(850, 500);
        setLocationRelativeTo(null);

        initUI();
        configurarTabla();
        recargarTabla();
    }

    private void initUI() {
        JLabel lbl = new JLabel("Gestión de Servicios");
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 22f));
        lbl.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        
        btnAgregar = new JButton("Agregar Servicio"); // Solo el botón de agregar
        btnAgregar.addActionListener(e -> mostrarDialogoAgregar(null)); 
        
        JPanel north = new JPanel(new BorderLayout());
        north.add(lbl, BorderLayout.WEST);
        
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.add(btnAgregar); 
        
        north.add(right, BorderLayout.EAST);
        
        tabla = new JTable(model);
        JScrollPane sp = new JScrollPane(tabla);
        
        JPanel root = new JPanel(new BorderLayout(8,8));
        root.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        root.add(north, BorderLayout.NORTH);
        root.add(sp, BorderLayout.CENTER);
        
        setContentPane(root);
    }
    
    private void configurarTabla() {
        tabla.setRowHeight(36); 
        tabla.setFillsViewportHeight(true);
        tabla.setAutoCreateRowSorter(true);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        
        var cols = tabla.getColumnModel();
        
        if (cols.getColumnCount() > ServiciosTableModel.COL_ACCIONES) {
             // Configuración de Anchos
            cols.getColumn(0).setPreferredWidth(60);  // ID
            cols.getColumn(1).setPreferredWidth(150); // Nombre
            cols.getColumn(2).setPreferredWidth(100); // Precio
            cols.getColumn(3).setPreferredWidth(300); // Descripción
            cols.getColumn(4).setPreferredWidth(160); // Acciones 

            // Alineación
            cols.getColumn(0).setCellRenderer(centerRenderer);
            cols.getColumn(2).setCellRenderer(rightRenderer);

            // Añadir Renderer y Editor a la columna de Acciones (Índice 4)
            int colAcciones = ServiciosTableModel.COL_ACCIONES;
            var accionesCol = tabla.getColumnModel().getColumn(colAcciones);
            accionesCol.setCellRenderer(new AccionesRenderer());
            accionesCol.setCellEditor(new AccionesEditor());
        }
    }

    public void recargarTabla() {
        try {
            List<Servicios> lista = serviciosService.listarServicios();
            model.setData(lista);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar servicios: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // ... (rest of the Dialog methods: mostrarDialogoAgregar, onEditar, onEliminar logic)
    
    /**
     * Diálogo unificado para Agregar (servicio == null) o Editar (servicio != null).
     */
    private void mostrarDialogoAgregar(Servicios servicio) {
        boolean esEdicion = (servicio != null);
        String titulo = esEdicion ? "Editar Servicio" : "Agregar Nuevo Servicio";
        
        JDialog dlg = new JDialog(this, titulo, true);
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // Campos
        JTextField txtNombre = new JTextField(esEdicion ? servicio.getNombre() : "", 22);
        JFormattedTextField txtPrecio = new JFormattedTextField(NumberFormat.getNumberInstance());
        if (esEdicion) txtPrecio.setValue(servicio.getPrecio());
        JTextArea txtDescripcion = new JTextArea(esEdicion ? servicio.getDescripcion() : "", 3, 25);
        
        // Layout (GridBagLayout)
        JPanel content = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 10, 8, 10);
        g.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Fila 1: Nombre
        g.gridx=0; g.gridy=row; g.weightx=0; content.add(new JLabel("Nombre:"), g);
        g.gridx=1; g.weightx=1; content.add(txtNombre, g);
        
        // Fila 2: Precio
        row++; 
        g.gridx=0; g.gridy=row; g.weightx=0; content.add(new JLabel("Precio:"), g);
        g.gridx=1; g.weightx=1; content.add(txtPrecio, g);
        
        // Fila 3: Descripción (ocupa 3 filas de altura)
        row++; 
        g.gridx=0; g.gridy=row; g.weightx=0; content.add(new JLabel("Descripción:"), g);
        
        g.gridx=1; g.weightx=1; 
        g.gridheight=3; 
        content.add(new JScrollPane(txtDescripcion), g);
        
        g.gridheight=1; 
        
        // Fila 6 (row + 3): Botones
        row+=3; 
        
        JButton btnCancelar = new JButton("Cancelar");
        JButton btnGuardar = new JButton(esEdicion ? "Guardar Cambios" : "Agregar");
        
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnCancelar); botones.add(btnGuardar);
        
        g.gridx=0; g.gridy=row; g.gridwidth=2; g.weightx=1;
        content.add(botones, g);

        btnCancelar.addActionListener(e -> dlg.dispose());
        
        btnGuardar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String descripcion = txtDescripcion.getText().trim();
            BigDecimal precio = null;
            
            try {
                // Conversión del precio a BigDecimal
                Object value = txtPrecio.getValue();
                if (value instanceof Number) {
                    precio = new BigDecimal(value.toString());
                } else if (!txtPrecio.getText().trim().isEmpty()) {
                    Number num = NumberFormat.getNumberInstance().parse(txtPrecio.getText().trim());
                    precio = new BigDecimal(num.toString());
                } else {
                    throw new ParseException("El precio no puede estar vacío.", 0);
                }

                Servicios s = esEdicion ? servicio : new Servicios();
                
                s.setNombre(nombre);
                s.setDescripcion(descripcion);
                s.setPrecio(precio);

                if (esEdicion) {
                    serviciosService.actualizarServicio(s);
                    JOptionPane.showMessageDialog(this, "Servicio actualizado.");
                } else {
                    serviciosService.agregarServicio(s);
                    JOptionPane.showMessageDialog(this, "Servicio agregado.");
                }
                
                recargarTabla();
                dlg.dispose();
                
            } catch (IllegalArgumentException ex) { 
                JOptionPane.showMessageDialog(dlg, ex.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
            } catch (ParseException ex) {
                 JOptionPane.showMessageDialog(dlg, "Formato de precio inválido.", "Validación", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) { 
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dlg, "Error al guardar: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dlg.setContentPane(content);
        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    // ====== Renderer acciones (Pinta los botones en la celda) ======
    // COPIA DE CLIENTEVIEWFORM
    private static class AccionesRenderer extends JPanel implements TableCellRenderer {
        private final JButton btnEdit = new JButton("Editar");
        private final JButton btnDel  = new JButton("Eliminar");
        
        AccionesRenderer() {
            setOpaque(true);
            setLayout(new FlowLayout(FlowLayout.CENTER, 6, 0));
            btnEdit.setFocusable(false); btnDel.setFocusable(false);
            btnEdit.setMargin(new Insets(2,8,2,8));
            btnDel.setMargin(new Insets(2,8,2,8));
            add(btnEdit); add(btnDel);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return this;
        }
    }

    // ====== Editor acciones (Añade la lógica de los clics) ======
    // COPIA DE CLIENTEVIEWFORM
    private class AccionesEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        private final JButton btnEdit = new JButton("Editar");
        private final JButton btnDel  = new JButton("Eliminar");
        private int editingRow = -1;
        
        AccionesEditor() {
            btnEdit.setMargin(new Insets(2,8,2,8));
            btnDel.setMargin(new Insets(2,8,2,8));
            panel.add(btnEdit); panel.add(btnDel);
            
            // Asignar acciones a los botones
            btnEdit.addActionListener(e -> { onEditar();  fireEditingStopped(); });
            btnDel.addActionListener(e -> { onEliminar(); fireEditingStopped(); });
        }
        
        private void onEditar() {
            if (editingRow < 0) return;
            int modelRow = tabla.convertRowIndexToModel(editingRow);
            Servicios s = model.getAt(modelRow);
            mostrarDialogoAgregar(s); // Reutilizamos el diálogo de agregar para editar
        }

        private void onEliminar() {
            if (editingRow < 0) return;
            int modelRow = tabla.convertRowIndexToModel(editingRow);
            Servicios s = model.getAt(modelRow);

            int ok = JOptionPane.showConfirmDialog(
                    ServiciosViewForm.this,
                    "¿Eliminar el servicio \"" + s.getNombre() + "\"?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
            );
            
            if (ok == JOptionPane.YES_OPTION) {
                try {
                    serviciosService.eliminarServicio(s.getId());
                    recargarTabla();
                    JOptionPane.showMessageDialog(ServiciosViewForm.this, "Servicio eliminado.");
                } catch (Exception ex) { 
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(ServiciosViewForm.this,
                            "Error al eliminar: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        @Override 
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            editingRow = row;
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }
        
        @Override 
        public Object getCellEditorValue() { return null; }
    }

    // --- MAIN DE PRUEBA (mantener como está o modificar para tu inyector) ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            IServiciosRepository repo = new ServicioRepository();
            ServiciosService service = new ServiciosService(repo);
            new ServiciosViewForm(service).setVisible(true);
        });
    }
}