package com.veterinaria.veterinariaapp.ui;

import com.veterinaria.veterinariaapp.ui.ClienteTableModel;
import com.veterinaria.veterinariaapp.dao.ClienteDao;
import com.veterinaria.veterinariaapp.model.Cliente;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

/**
 *
 * @author Yuriko Matsuo
 */
public class ClienteViewForm extends javax.swing.JFrame {
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        JTResultadoCliente = new javax.swing.JTable();
        btnRegistroCliente = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        JTResultadoCliente.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Nombre", "Apellido", "Dirección", "Telefono", "Acciones"
            }
        ));
        JTResultadoCliente.setName(""); // NOI18N
        jScrollPane2.setViewportView(JTResultadoCliente);

        btnRegistroCliente.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnRegistroCliente.setText("Agregar Cliente");
        btnRegistroCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistroClienteActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setText("Clientes");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(76, 76, 76)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(779, 779, 779)
                        .addComponent(btnRegistroCliente)))
                .addContainerGap(130, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(138, 138, 138)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(btnRegistroCliente))
                .addGap(27, 27, 27)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(204, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRegistroClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistroClienteActionPerformed
        // TODO add your handling code here:
        mostrarDialogoAgregar();
    }//GEN-LAST:event_btnRegistroClienteActionPerformed
    // --- DAO y modelo de tabla (sin clases extra) ---
    private final ClienteDao dao = new ClienteDao();
    private final ClienteTableModel model = new ClienteTableModel(new ArrayList<>());
    
    private void mostrarDialogoAgregar() {
    JDialog dlg = new JDialog(this, "Agregar Cliente", true);
    dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    // Campos
    JTextField txtNombre    = new JTextField(22);
    JTextField txtApellido  = new JTextField(22);
    JTextField txtDireccion = new JTextField(26);
    JTextField txtTelefono  = new JTextField(14);

    // Layout del diálogo
    JPanel content = new JPanel(new GridBagLayout());
    GridBagConstraints g = new GridBagConstraints();
    g.insets = new Insets(8, 10, 8, 10);
    g.fill = GridBagConstraints.HORIZONTAL;

    int row = 0;
    // Título sección
    JLabel lblTitulo = new JLabel("Nombre del cliente");
    lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD));
    g.gridx = 0; g.gridy = row; g.gridwidth = 2;
    content.add(lblTitulo, g);

    // Nombre
    row++;
    g.gridy = row; g.gridwidth = 1; g.weightx = 0;
    content.add(new JLabel("Nombre:"), g);
    g.gridx = 1; g.weightx = 1;
    content.add(txtNombre, g);

    // Subtítulo
    row++; g.gridx = 0; g.gridy = row; g.gridwidth = 2; g.weightx = 0;
    JLabel lblInfo = new JLabel("Información personal");
    lblInfo.setFont(lblInfo.getFont().deriveFont(Font.BOLD));
    content.add(lblInfo, g);

    // Apellido
    row++; g.gridy = row; g.gridwidth = 1; g.gridx = 0; g.weightx = 0;
    content.add(new JLabel("Apellido:"), g);
    g.gridx = 1; g.weightx = 1;
    content.add(txtApellido, g);

    // Dirección
    row++; g.gridy = row; g.gridx = 0; g.weightx = 0;
    content.add(new JLabel("Dirección:"), g);
    g.gridx = 1; g.weightx = 1;
    content.add(txtDireccion, g);

    // Teléfono
    row++; g.gridy = row; g.gridx = 0; g.weightx = 0;
    content.add(new JLabel("Teléfono:"), g);
    g.gridx = 1; g.weightx = 1;
    content.add(txtTelefono, g);

    // Botones
    JButton btnCancelar = new JButton("Cancelar");
    JButton btnAgregar  = new JButton("Agregar");
    JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    botones.add(btnCancelar);
    botones.add(btnAgregar);

    row++; g.gridx = 0; g.gridy = row; g.gridwidth = 2; g.weightx = 1;
    content.add(botones, g);

    // Listeners
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
            dao.registrar(c);         // guarda en MySQL
            recargarTabla();          // refresca la grilla
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

    public ClienteViewForm() {
    initComponents();     // NO tocar: es el del diseñador
    configurarTabla();    // conecta JTable con el modelo y ajusta estilos
    recargarTabla();      // llena datos desde MySQL
    }

    private void configurarTabla() {
    JTResultadoCliente.setModel(model); // ← usar tu ClienteTableModel

    JTResultadoCliente.setRowHeight(36);
    JTResultadoCliente.setFillsViewportHeight(true);
    JTResultadoCliente.setAutoCreateRowSorter(true);
    JTResultadoCliente.getTableHeader().setReorderingAllowed(false);
    JTResultadoCliente.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    // Centrar ID y Teléfono y ajustar anchos
    DefaultTableCellRenderer center = new DefaultTableCellRenderer();
    center.setHorizontalAlignment(SwingConstants.CENTER);

    var cols = JTResultadoCliente.getColumnModel();
    if (cols.getColumnCount() >= 6) { // hay 6: ID, Nombre, Apellido, Dirección, Teléfono, Acciones
        cols.getColumn(0).setPreferredWidth(50);   // ID
        cols.getColumn(1).setPreferredWidth(140);  // Nombre
        cols.getColumn(2).setPreferredWidth(140);  // Apellido
        cols.getColumn(3).setPreferredWidth(260);  // Dirección
        cols.getColumn(4).setPreferredWidth(120);  // Teléfono
        cols.getColumn(5).setPreferredWidth(120);  // Acciones (vacío por ahora)

        cols.getColumn(0).setCellRenderer(center);
        cols.getColumn(4).setCellRenderer(center);
    }
    
    // === Columna Acciones: renderer + editor ===
    int colAcciones = JTResultadoCliente.getColumnModel().getColumnIndex("Acciones");
    var accionesCol = JTResultadoCliente.getColumnModel().getColumn(colAcciones);
    accionesCol.setMinWidth(170);
    accionesCol.setPreferredWidth(190);
    accionesCol.setMaxWidth(220);
    accionesCol.setCellRenderer(new AccionesRenderer());
    accionesCol.setCellEditor(new AccionesEditor());
}
private void recargarTabla() {
    try {
        model.setData(dao.listar());  // ← carga directa al modelo personalizado
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

// ====== RENDERER: pinta dos botones (sin acciones; solo visual) ======
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

// ====== EDITOR: mismos botones pero con acciones reales ======
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
        int modelRow = JTResultadoCliente.convertRowIndexToModel(editingRow);
        Cliente c = model.getAt(modelRow);
        mostrarDialogoEditar(c); // abre dialogo y hace dao.actualizar(...)
    }

    private void onEliminar() {
        if (editingRow < 0) return;
        int modelRow = JTResultadoCliente.convertRowIndexToModel(editingRow);
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
    g.gridx=0; g.gridy=row; g.weightx=0; content.add(new JLabel("Nombre:"), g);
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
        String nombre = txtNombre.getText().trim();
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

public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new ClienteViewForm().setVisible(true));
}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable JTResultadoCliente;
    private javax.swing.JButton btnRegistroCliente;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
