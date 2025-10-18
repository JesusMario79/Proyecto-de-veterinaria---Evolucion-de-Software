package com.veterinaria.veterinariaapp.ui;

// ¡CAMBIOS EN IMPORTS!
// import com.veterinaria.veterinariaapp.dao.ClienteDao; // <-- ELIMINADO
import com.veterinaria.veterinariaapp.model.Cliente;
import com.veterinaria.veterinariaapp.service.ClienteService; // <-- AÑADIDO
import com.veterinaria.veterinariaapp.repository.ClienteRepository; // Para el 'main'
import com.veterinaria.veterinariaapp.repository.IClienteRepository; // Para el 'main'


import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteViewForm extends JFrame {

    // ¡CORREGIDO!
    private final ClienteService clienteService; // <-- AÑADIDO
    private final ClienteTableModel model = new ClienteTableModel(new ArrayList<>());

    private JTable tabla;
    private JButton btnAgregar;

    // ¡CORREGIDO! (Recibe el servicio)
    public ClienteViewForm(ClienteService clienteService) {
        this.clienteService = clienteService; 

        setTitle("Clientes");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        initUI();
        configurarTabla();
        recargarTabla();
    }

    private void initUI() {
        // (Este método no cambia)
        JLabel lbl = new JLabel("Clientes");
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 22f));
        lbl.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        btnAgregar = new JButton("Agregar Cliente");
        btnAgregar.addActionListener(e -> mostrarDialogoAgregar());
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
        // (Este método no cambia)
        tabla.setRowHeight(36);
        tabla.setFillsViewportHeight(true);
        tabla.setAutoCreateRowSorter(true);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        var cols = tabla.getColumnModel();
        if (cols.getColumnCount() > ClienteTableModel.COL_ACCIONES) {
            cols.getColumn(0).setPreferredWidth(60);
            cols.getColumn(1).setPreferredWidth(140);
            cols.getColumn(2).setPreferredWidth(140);
            cols.getColumn(3).setPreferredWidth(260);
            cols.getColumn(4).setPreferredWidth(120);
            
            cols.getColumn(0).setCellRenderer(center);
            cols.getColumn(4).setCellRenderer(center);

            int colAcciones = tabla.getColumnModel().getColumnIndex("Acciones");
            var accionesCol = tabla.getColumnModel().getColumn(colAcciones);
            accionesCol.setMinWidth(160);
            accionesCol.setMaxWidth(220);
            accionesCol.setCellRenderer(new AccionesRenderer());
            accionesCol.setCellEditor(new AccionesEditor());
        }
    }

    public void recargarTabla() {
        try {
            // ¡CORREGIDO!
            List<Cliente> lista = clienteService.listarClientes(); // Llama al servicio
            model.setData(lista);
            configurarTabla(); 
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar clientes: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarDialogoAgregar() {
        JDialog dlg = new JDialog(this, "Agregar Cliente", true);
        // (El layout del diálogo no cambia)
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        JTextField txtNombre    = new JTextField(22);
        JTextField txtApellido  = new JTextField(22);
        JTextField txtDireccion = new JTextField(26);
        JTextField txtTelefono  = new JTextField(14);
        JPanel content = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 10, 8, 10);
        g.fill = GridBagConstraints.HORIZONTAL;
        int row = 0;
        g.gridx=0; g.gridy=row; content.add(new JLabel("Nombre:"), g);
        g.gridx=1; g.weightx=1; content.add(txtNombre, g);
        row++; g.gridx=0; g.gridy=row; g.weightx=0; content.add(new JLabel("Apellido:"), g);
        g.gridx=1; g.weightx=1; content.add(txtApellido, g);
        row++; g.gridx=0; g.gridy=row; g.weightx=0; content.add(new JLabel("Dirección:"), g);
        g.gridx=1; g.weightx=1; content.add(txtDireccion, g);
        row++; g.gridx=0; g.gridy=row; g.weightx=0; content.add(new JLabel("Teléfono:"), g);
        g.gridx=1; g.weightx=1; content.add(txtTelefono, g);
        JButton btnCancelar = new JButton("Cancelar");
        JButton btnAgregar  = new JButton("Agregar");
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnCancelar); botones.add(btnAgregar);
        row++; g.gridx=0; g.gridy=row; g.gridwidth=2; g.weightx=1;
        content.add(botones, g);

        btnCancelar.addActionListener(e -> dlg.dispose());
        
        // ¡LÓGICA DE BOTÓN CORREGIDA!
        btnAgregar.addActionListener(e -> {
            String nombre    = txtNombre.getText().trim();
            String apellido  = txtApellido.getText().trim();
            String direccion = txtDireccion.getText().trim();
            String telefono  = txtTelefono.getText().trim();
            
            try {
                Cliente c = new Cliente(nombre, apellido, direccion, telefono);
                
                // Delegar Lógica de Negocio y Guardado al Servicio
                clienteService.agregarCliente(c); // <-- SRP y DIP
                
                recargarTabla();
                JOptionPane.showMessageDialog(this, "Cliente agregado.");
                dlg.dispose();
                
            } catch (IllegalArgumentException ex) { // Errores de NEGOCIO
                JOptionPane.showMessageDialog(dlg, ex.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) { // Errores de BD
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

    private void mostrarDialogoEditar(Cliente c) {
        JDialog dlg = new JDialog(this, "Editar Cliente", true);
        // (El layout del diálogo no cambia)
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        JTextField txtNombre    = new JTextField(c.getNombre(), 22);
        JTextField txtApellido  = new JTextField(c.getApellido(), 22);
        JTextField txtDireccion = new JTextField(c.getDireccion(), 26);
        JTextField txtTelefono  = new JTextField(c.getTelefono(), 14);
        JPanel content = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 10, 8, 10);
        g.fill = GridBagConstraints.HORIZONTAL;
        int row = 0;
        g.gridx=0; g.gridy=row; content.add(new JLabel("Nombre:"), g);
        g.gridx=1; g.weightx=1; content.add(txtNombre, g);
        row++; g.gridx=0; g.gridy=row; g.weightx=0; content.add(new JLabel("Apellido:"), g);
        g.gridx=1; g.weightx=1; content.add(txtApellido, g);
        row++; g.gridx=0; g.gridy=row; g.weightx=0; content.add(new JLabel("Dirección:"), g);
        g.gridx=1; g.weightx=1; content.add(txtDireccion, g);
        row++; g.gridx=0; g.gridy=row; g.weightx=0; content.add(new JLabel("Teléfono:"), g);
        g.gridx=1; g.weightx=1; content.add(txtTelefono, g);
        JButton btnCancelar = new JButton("Cancelar");
        JButton btnGuardar  = new JButton("Guardar");
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnCancelar); botones.add(btnGuardar);
        row++; g.gridx=0; g.gridy=row; g.gridwidth=2; content.add(botones, g);


        btnCancelar.addActionListener(e -> dlg.dispose());
        
        // ¡LÓGICA DE BOTÓN CORREGIDA!
        btnGuardar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String apellido = txtApellido.getText().trim();

            try {
                c.setNombre(nombre);
                c.setApellido(apellido);
                c.setDireccion(txtDireccion.getText().trim());
                c.setTelefono(txtTelefono.getText().trim());
                
                // Delegar Lógica de Negocio y Actualización al Servicio
                clienteService.actualizarCliente(c); // <-- SRP y DIP
                
                recargarTabla();
                JOptionPane.showMessageDialog(this, "Cliente actualizado.");
                dlg.dispose();
                
            } catch (IllegalArgumentException ex) { // Errores de NEGOCIO
                JOptionPane.showMessageDialog(dlg, ex.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) { // Errores de BD
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dlg, "Error al actualizar: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dlg.setContentPane(content);
        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    // ====== Renderer acciones (ver) ======
    private static class AccionesRenderer extends JPanel implements TableCellRenderer {
        // (Tu código no cambia)
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

    // ====== Editor acciones (lógica editar/eliminar) ======
    private class AccionesEditor extends AbstractCellEditor implements TableCellEditor {
        // (Tu código de constructor y 'onEditar' no cambia)
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        private final JButton btnEdit = new JButton("Editar");
        private final JButton btnDel  = new JButton("Eliminar");
        private int editingRow = -1;
        AccionesEditor() {
            btnEdit.setMargin(new Insets(2,8,2,8));
            btnDel.setMargin(new Insets(2,8,2,8));
            panel.add(btnEdit); panel.add(btnDel);
            btnEdit.addActionListener(e -> { onEditar();  fireEditingStopped(); });
            btnDel.addActionListener(e -> { onEliminar(); fireEditingStopped(); });
        }
        private void onEditar() {
            if (editingRow < 0) return;
            int modelRow = tabla.convertRowIndexToModel(editingRow);
            Cliente c = model.getAt(modelRow);
            mostrarDialogoEditar(c);
        }

        // ¡LÓGICA DE onEliminar CORREGIDA!
        private void onEliminar() {
            if (editingRow < 0) return;
            int modelRow = tabla.convertRowIndexToModel(editingRow);
            Cliente c = model.getAt(modelRow);

            int ok = JOptionPane.showConfirmDialog(
                    ClienteViewForm.this,
                    "¿Eliminar al cliente \"" + c.getNombre() + " " + c.getApellido() + "\"?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
            );
            if (ok == JOptionPane.YES_OPTION) {
                try {
                    // Delegar Lógica de Eliminación al Servicio
                    clienteService.eliminarCliente(c.getIdCliente()); // <-- SRP y DIP
                    
                    recargarTabla();
                    JOptionPane.showMessageDialog(ClienteViewForm.this, "Cliente eliminado.");
                    
                } catch (Exception ex) { // Errores de BD
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(ClienteViewForm.this,
                            "Error al eliminar: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        @Override public Component getTableCellEditorComponent(JTable table, Object value,
                                                               boolean isSelected, int row, int column) {
            editingRow = row;
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }
        @Override public Object getCellEditorValue() { return null; }
    }

    // ¡MAIN DE PRUEBA CORREGIDO!
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // --- ¡ENSAMBLAJE DE PRUEBA! ---
            // Así es como se debe construir esta vista ahora
            IClienteRepository repo = new ClienteRepository(); 
            ClienteService service = new ClienteService(repo);
            new ClienteViewForm(service).setVisible(true);
        });
    }
}