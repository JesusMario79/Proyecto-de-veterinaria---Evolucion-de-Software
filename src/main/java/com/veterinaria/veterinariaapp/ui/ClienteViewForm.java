package com.veterinaria.veterinariaapp.ui;

import com.veterinaria.veterinariaapp.dao.ClienteDao;
import com.veterinaria.veterinariaapp.model.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteViewForm extends JFrame {

    // DAO y modelo
    private final ClienteDao dao = new ClienteDao(); // Debe existir con: listar(), registrar(c), actualizar(c), eliminar(id)
    private final ClienteTableModel model = new ClienteTableModel(new ArrayList<>());

    // UI
    private JTable tabla;
    private JButton btnAgregar;

    public ClienteViewForm() {
        setTitle("Clientes");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE); // importante para embebido
        setSize(900, 600);
        setLocationRelativeTo(null);

        initUI();
        configurarTabla();
        recargarTabla();
    }

    private void initUI() {
        // Título
        JLabel lbl = new JLabel("Clientes");
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 22f));
        lbl.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Botón agregar
        btnAgregar = new JButton("Agregar Cliente");
        btnAgregar.addActionListener(e -> mostrarDialogoAgregar());

        JPanel north = new JPanel(new BorderLayout());
        north.add(lbl, BorderLayout.WEST);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.add(btnAgregar);
        north.add(right, BorderLayout.EAST);

        // Tabla
        tabla = new JTable(model);
        JScrollPane sp = new JScrollPane(tabla);

        // Layout principal
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
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

        // Centrar ID y Teléfono
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        var cols = tabla.getColumnModel();
        if (cols.getColumnCount() >= 6) {
            cols.getColumn(0).setPreferredWidth(60);   // ID
            cols.getColumn(1).setPreferredWidth(140);  // Nombre
            cols.getColumn(2).setPreferredWidth(140);  // Apellido
            cols.getColumn(3).setPreferredWidth(260);  // Dirección
            cols.getColumn(4).setPreferredWidth(120);  // Teléfono
            cols.getColumn(5).setPreferredWidth(180);  // Acciones

            cols.getColumn(0).setCellRenderer(center);
            cols.getColumn(4).setCellRenderer(center);

            // Columna Acciones con renderer + editor (editar/eliminar)
            int colAcciones = tabla.getColumnModel().getColumnIndex("Acciones");
            var accionesCol = tabla.getColumnModel().getColumn(colAcciones);
            accionesCol.setMinWidth(160);
            accionesCol.setMaxWidth(220);
            accionesCol.setCellRenderer(new AccionesRenderer());
            accionesCol.setCellEditor(new AccionesEditor());
        }
    }

    private void recargarTabla() {
        try {
            List<Cliente> lista = dao.listar();
            model.setData(lista);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Error al cargar clientes: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ============ Diálogo: Agregar ============
    private void mostrarDialogoAgregar() {
        JDialog dlg = new JDialog(this, "Agregar Cliente", true);
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
        btnAgregar.addActionListener(e -> {
            String nombre    = txtNombre.getText().trim();
            String apellido  = txtApellido.getText().trim();
            String direccion = txtDireccion.getText().trim();
            String telefono  = txtTelefono.getText().trim();

            if (nombre.isEmpty() || apellido.isEmpty()) {
                JOptionPane.showMessageDialog(dlg,
                        "Nombre y Apellido son obligatorios.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Cliente c = new Cliente(nombre, apellido, direccion, telefono);
                dao.registrar(c);
                recargarTabla();
                JOptionPane.showMessageDialog(this, "Cliente agregado.");
                dlg.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dlg,
                        "Error al guardar: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dlg.setContentPane(content);
        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    // ============ Diálogo: Editar ============
    private void mostrarDialogoEditar(Cliente c) {
        JDialog dlg = new JDialog(this, "Editar Cliente", true);
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
        btnGuardar.addActionListener(e -> {
            String nombre   = txtNombre.getText().trim();
            String apellido = txtApellido.getText().trim();
            if (nombre.isEmpty() || apellido.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Nombre y Apellido son obligatorios.");
                return;
            }
            try {
                c.setNombre(nombre);
                c.setApellido(apellido);
                c.setDireccion(txtDireccion.getText().trim());
                c.setTelefono(txtTelefono.getText().trim());
                dao.actualizar(c);
                recargarTabla();
                JOptionPane.showMessageDialog(this, "Cliente actualizado.");
                dlg.dispose();
            } catch (Exception ex) {
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

    // ====== Renderer acciones (ver solo) ======
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

    // ====== Editor acciones (con lógica editar/eliminar) ======
    private class AccionesEditor extends AbstractCellEditor implements TableCellEditor {
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
                    dao.eliminar(c.getIdCliente());
                    recargarTabla();
                    JOptionPane.showMessageDialog(ClienteViewForm.this, "Cliente eliminado.");
                } catch (Exception ex) {
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

    // Ejecutable independiente (opcional)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClienteViewForm().setVisible(true));
    }
}
