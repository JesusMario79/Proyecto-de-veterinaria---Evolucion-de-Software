package com.veterinaria.veterinariaapp.ui;

import com.veterinaria.veterinariaapp.model.Mascota;
import com.veterinaria.veterinariaapp.repository.MascotaRepository;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MascotaViewForm extends JFrame {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(MascotaViewForm.class.getName());

    private javax.swing.JTable JTResultadoMascota;
    private javax.swing.JButton btnRegistroMascota;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;

    private final List<Mascota> fuente = new ArrayList<>();
    private final MascotaTableModel model = new MascotaTableModel(fuente);
    private final MascotaRepository mascotaRepo = new MascotaRepository();

    public MascotaViewForm() {
        initComponents();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE); // clave para embebido
        configurarTabla();
        recargarTabla();
        btnRegistroMascota.addActionListener(e -> abrirDialogoAgregar());
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        JTResultadoMascota = new javax.swing.JTable();
        btnRegistroMascota = new javax.swing.JButton();

        setTitle("Mascotas");

         // ======= Título =======
        jLabel1.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 24));
        jLabel1.setText("Mascotas");

        JTResultadoMascota.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {{},{},{},{}},
            new String [] {"ID","Imagen","Nombre de la mascota","Fecha de Registro","Fecha de Nacimiento","Raza","Especie","Cliente","Acciones"}
        ));
        jScrollPane1.setViewportView(JTResultadoMascota);

        btnRegistroMascota.setText("Agregar Mascota");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(layout.createSequentialGroup()
                .addGap(16,16,16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addGroup(layout.createSequentialGroup()
                    .addComponent(jLabel1)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 700, Short.MAX_VALUE)
                    .addComponent(btnRegistroMascota))
                  .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 980, Short.MAX_VALUE))
                .addGap(16,16,16))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(layout.createSequentialGroup()
                .addGap(16,16,16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                  .addComponent(jLabel1)
                  .addComponent(btnRegistroMascota))
                .addGap(12,12,12)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE)
                .addGap(16,16,16))
        );

        pack();
    }

    private void configurarTabla() {
        JTResultadoMascota.setModel(model);

        JTResultadoMascota.setRowHeight(56);
        JTResultadoMascota.setFillsViewportHeight(true);
        JTResultadoMascota.setAutoCreateRowSorter(true);
        JTResultadoMascota.getTableHeader().setReorderingAllowed(false);
        JTResultadoMascota.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        var cols = JTResultadoMascota.getColumnModel();
        if (cols.getColumnCount() >= 9) {
            cols.getColumn(0).setPreferredWidth(50);
            cols.getColumn(1).setPreferredWidth(80);
            cols.getColumn(2).setPreferredWidth(180);
            cols.getColumn(3).setPreferredWidth(120);
            cols.getColumn(4).setPreferredWidth(130);
            cols.getColumn(5).setPreferredWidth(120);
            cols.getColumn(6).setPreferredWidth(120);
            cols.getColumn(7).setPreferredWidth(180);

            int colAccionesIndex = JTResultadoMascota.getColumnModel().getColumnIndex("Acciones");
            var colAcc = cols.getColumn(colAccionesIndex);
            colAcc.setMinWidth(170);
            colAcc.setPreferredWidth(190);
            colAcc.setMaxWidth(220);

            var acciones = new AccionesCell(this::editarMascota, this::eliminarMascota);
            colAcc.setCellRenderer(acciones);
            colAcc.setCellEditor(acciones);
        }

        if (JTResultadoMascota.getRowSorter() == null) {
            JTResultadoMascota.setRowSorter(new javax.swing.table.TableRowSorter<>(JTResultadoMascota.getModel()));
        }
        var sorter = (javax.swing.table.TableRowSorter<?>) JTResultadoMascota.getRowSorter();
        java.util.List<javax.swing.RowSorter.SortKey> keys = new java.util.ArrayList<>();
        keys.add(new javax.swing.RowSorter.SortKey(0, javax.swing.SortOrder.ASCENDING));
        sorter.setSortKeys(keys);
        sorter.sort();
    }

    public void recargarTabla() {
        try {
            var datos = mascotaRepo.listar();
            model.setData(datos);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar mascotas: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirDialogoAgregar() {
        Mascota m = new Mascota();
        boolean ok = mostrarDialogoMascota(m, true);
        if (ok) {
            try {
                mascotaRepo.insertar(m);
                recargarTabla();
                JOptionPane.showMessageDialog(this, "Mascota agregada.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editarMascota(int viewRow) {
        int modelRow = JTResultadoMascota.convertRowIndexToModel(viewRow);
        Mascota m = model.getAt(modelRow);
        boolean ok = mostrarDialogoMascota(m, false);
        if (ok) {
            try {
                mascotaRepo.actualizar(m);
                recargarTabla();
                JOptionPane.showMessageDialog(this, "Mascota actualizada.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al actualizar: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void eliminarMascota(int viewRow) {
        int modelRow = JTResultadoMascota.convertRowIndexToModel(viewRow);
        Mascota m = model.getAt(modelRow);

        int ok = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar a la mascota \"" + m.getNombre() + "\"?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (ok == JOptionPane.YES_OPTION) {
            try {
                mascotaRepo.eliminar(m.getId());
                recargarTabla();
                JOptionPane.showMessageDialog(this, "Mascota eliminada.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ====== Combo Clientes ======
    private static class ClienteItem {
        final Integer id;
        final String label;
        ClienteItem(Integer id, String label) { this.id = id; this.label = label; }
        @Override public String toString() { return label; }
    }

    private List<ClienteItem> cargarClientesDesdeBD() {
        String sql = "SELECT id_cliente, CONCAT(nombre,' ',apellido) AS label " +
                     "FROM cliente ORDER BY nombre, apellido";
        List<ClienteItem> out = new ArrayList<>();
        try (var cn = com.veterinaria.veterinariaapp.config.Db.getConnection();
             var ps = cn.prepareStatement(sql);
             var rs = ps.executeQuery()) {
            while (rs.next()) out.add(new ClienteItem(rs.getInt("id_cliente"), rs.getString("label")));
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "No pude cargar clientes: " + ex.getMessage(),
                    "Clientes", JOptionPane.WARNING_MESSAGE);
        }
        return out;
    }

    // ====== Diálogo Mascota ======
    private boolean mostrarDialogoMascota(Mascota m, boolean esNuevo) {
        JDialog dlg = new JDialog(this, esNuevo ? "Agregar Mascota" : "Editar Mascota", true);
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JTextField txtNombre  = new JTextField(m.getNombre()  != null ? m.getNombre()  : "", 22);
        JTextField txtEspecie = new JTextField(m.getEspecie() != null ? m.getEspecie() : "", 22);
        JTextField txtRaza    = new JTextField(m.getRaza()    != null ? m.getRaza()    : "", 22);

        var clientes = cargarClientesDesdeBD();
        JComboBox<ClienteItem> cbCliente = new JComboBox<>(clientes.toArray(new ClienteItem[0]));
        if (m.getClienteId() != null) {
            for (int i = 0; i < cbCliente.getItemCount(); i++) {
                if (cbCliente.getItemAt(i).id.equals(m.getClienteId())) { cbCliente.setSelectedIndex(i); break; }
            }
        }

        java.util.function.Function<LocalDate, Date> toDate = ld ->
                ld == null ? null : Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
        java.util.function.Function<Date, LocalDate> toLocal = d ->
                d == null ? null : d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        var modReg = new javax.swing.SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH);
        var modNac = new javax.swing.SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH);

        Date dReg = toDate.apply(m.getFechaRegistro() != null ? m.getFechaRegistro() : LocalDate.now());
        if (dReg != null) modReg.setValue(dReg);
        Date dNac = toDate.apply(m.getFechaNacimiento());
        if (dNac != null) modNac.setValue(dNac);

        JSpinner spFecReg = new JSpinner(modReg);
        JSpinner spFecNac = new JSpinner(modNac);
        spFecReg.setEditor(new JSpinner.DateEditor(spFecReg, "yyyy-MM-dd"));
        spFecNac.setEditor(new JSpinner.DateEditor(spFecNac, "yyyy-MM-dd"));

        JLabel lblPreview = new JLabel();
        lblPreview.setHorizontalAlignment(SwingConstants.CENTER);
        lblPreview.setPreferredSize(new Dimension(96, 96));
        if (m.getFoto() != null && m.getFoto().length > 0) lblPreview.setIcon(iconFromBytes(m.getFoto(), 96, 96));
        JButton btnSeleccionar = new JButton("Seleccionar foto…");
        final byte[][] fotoHolder = new byte[][]{ m.getFoto() };
        btnSeleccionar.addActionListener(e -> {
            byte[] bytes = seleccionarImagenBytes();
            if (bytes != null) {
                fotoHolder[0] = bytes;
                lblPreview.setIcon(iconFromBytes(bytes, 96, 96));
            }
        });

        JPanel content = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 10, 8, 10);
        g.fill = GridBagConstraints.HORIZONTAL;
        int row = 0;

        g.gridx = 0; g.gridy = row; content.add(new JLabel("Nombre:"), g);
        g.gridx = 1; content.add(txtNombre, g);

        row++; g.gridx = 0; g.gridy = row; content.add(new JLabel("Especie:"), g);
        g.gridx = 1; content.add(txtEspecie, g);

        row++; g.gridx = 0; g.gridy = row; content.add(new JLabel("Raza:"), g);
        g.gridx = 1; content.add(txtRaza, g);

        row++; g.gridx = 0; g.gridy = row; content.add(new JLabel("Cliente:"), g);
        g.gridx = 1; content.add(cbCliente, g);

        row++; g.gridx = 0; g.gridy = row; content.add(new JLabel("Fecha de Registro (yyyy-MM-dd):"), g);
        g.gridx = 1; content.add(spFecReg, g);

        row++; g.gridx = 0; g.gridy = row; content.add(new JLabel("Fecha de Nacimiento (yyyy-MM-dd):"), g);
        g.gridx = 1; content.add(spFecNac, g);

        row++; g.gridx = 0; g.gridy = row; content.add(new JLabel("Foto:"), g);
        JPanel pFoto = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pFoto.add(lblPreview); pFoto.add(btnSeleccionar);
        g.gridx = 1; content.add(pFoto, g);

        JButton btnCancelar = new JButton("Cancelar");
        JButton btnGuardar  = new JButton(esNuevo ? "Agregar" : "Guardar");
        JPanel pBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pBtns.add(btnCancelar); pBtns.add(btnGuardar);
        row++; g.gridx = 0; g.gridy = row; g.gridwidth = 2; g.fill = GridBagConstraints.NONE; g.anchor = GridBagConstraints.EAST;
        content.add(pBtns, g);

        final boolean[] saved = { false };
        btnCancelar.addActionListener(e -> dlg.dispose());
        btnGuardar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "El nombre es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            LocalDate fr = toLocal.apply((Date) spFecReg.getValue());
            LocalDate fn = toLocal.apply((Date) spFecNac.getValue());
            ClienteItem sel = (ClienteItem) cbCliente.getSelectedItem();
            Integer clienteId = sel != null ? sel.id : null;
            String clienteNombre = sel != null ? sel.label : null;

            m.setNombre(nombre);
            m.setEspecie(txtEspecie.getText().trim());
            m.setRaza(txtRaza.getText().trim());
            m.setFechaRegistro(fr);
            m.setFechaNacimiento(fn);
            m.setClienteId(clienteId);
            m.setClienteNombre(clienteNombre);
            m.setFoto(fotoHolder[0]);

            saved[0] = true;
            dlg.dispose();
        });

        dlg.setContentPane(content);
        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
        return saved[0];
    }

    private static ImageIcon iconFromBytes(byte[] imgBytes, int w, int h) {
        if (imgBytes == null || imgBytes.length == 0) return null;
        Image img = new ImageIcon(imgBytes).getImage()
                .getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private static byte[] seleccionarImagenBytes() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Seleccionar imagen");
        int r = fc.showOpenDialog(null);
        if (r == JFileChooser.APPROVE_OPTION) {
            java.io.File f = fc.getSelectedFile();
            try {
                return java.nio.file.Files.readAllBytes(f.toPath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,
                        "No se pudo leer la imagen: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }

    // =================== Renderer/Editor para Acciones ===================
private static class AccionesCell extends AbstractCellEditor
        implements TableCellRenderer, TableCellEditor {

    private final JPanel panel = new JPanel(new GridBagLayout()); // <-- centrado vertical
    private final JButton btnEdit = new JButton("Editar");
    private final JButton btnDel  = new JButton("Eliminar");
    private final java.util.function.IntConsumer onEditar;
    private final java.util.function.IntConsumer onEliminar;
    private int fila = -1;

    AccionesCell(java.util.function.IntConsumer onEditar,
                 java.util.function.IntConsumer onEliminar) {
        this.onEditar = onEditar;
        this.onEliminar = onEliminar;

        btnEdit.setMargin(new Insets(4,10,4,10));
        btnDel.setMargin(new Insets(4,10,4,10));
        btnEdit.setFocusable(false);
        btnDel.setFocusable(false);

        // Panel interno centrado horizontal y verticalmente
        JPanel inner = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        inner.setOpaque(false);
        inner.add(btnEdit);
        inner.add(btnDel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(inner, gbc);

        // Acciones de botones
        btnEdit.addActionListener(e -> {
            if (fila >= 0) onEditar.accept(fila);
            fireEditingStopped();
        });
        btnDel.addActionListener(e -> {
            if (fila >= 0) onEliminar.accept(fila);
            fireEditingStopped();
        });
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        panel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        return panel;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        this.fila = row;
        panel.setBackground(table.getSelectionBackground());
        return panel;
    }

    @Override public Object getCellEditorValue() { return null; }
    }
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MascotaViewForm().setVisible(true));
    }
}