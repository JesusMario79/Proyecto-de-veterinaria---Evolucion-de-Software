package com.veterinaria.veterinariaapp.ui;

import com.veterinaria.veterinariaapp.model.Producto;
import com.veterinaria.veterinariaapp.repository.IProductoRepository;
import com.veterinaria.veterinariaapp.repository.ProductoRepository;
import com.veterinaria.veterinariaapp.service.ProductoService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer; // Importante para centrar
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Vista Swing para gestionar Productos (Inventario).
 * Usa ProductoService como capa de negocio y ProductoTableModel para la tabla.
 */
public class ProductosViewForm extends JFrame {

    private final ProductoService productoService;
    private final ProductoTableModel model = new ProductoTableModel();

    private JTable tablaProductos;
    private JButton btnNuevo;

    public ProductosViewForm(ProductoService productoService) {
        this.productoService = productoService;

        setTitle("Productos");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(950, 600);
        setLocationRelativeTo(null);

        initUI();
        configurarTabla();
        recargarTabla();
    }

    // ================== UI BÁSICA ==================
    private void initUI() {
        JLabel lblTitulo = new JLabel("Productos");
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 22f));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        btnNuevo = new JButton("Agregar Producto");
        btnNuevo.addActionListener(e -> abrirDialogoAgregar());

        JPanel north = new JPanel(new BorderLayout());
        north.add(lblTitulo, BorderLayout.WEST);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.add(btnNuevo);
        north.add(right, BorderLayout.EAST);

        tablaProductos = new JTable(model);
        JScrollPane scroll = new JScrollPane(tablaProductos);

        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        root.add(north, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);

        setContentPane(root);
    }

    // ================== TABLA ==================
    private void configurarTabla() {
        tablaProductos.setRowHeight(32);
        tablaProductos.setFillsViewportHeight(true);
        tablaProductos.setAutoCreateRowSorter(true);
        tablaProductos.getTableHeader().setReorderingAllowed(false);
        tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 1. Crear Renderizador para CENTRAR texto
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        var cols = tablaProductos.getColumnModel();
        if (cols.getColumnCount() > ProductoTableModel.COL_ACC) {
            // Anchos de columnas
            cols.getColumn(ProductoTableModel.COL_ID).setPreferredWidth(40);
            cols.getColumn(ProductoTableModel.COL_NOMBRE).setPreferredWidth(180);
            cols.getColumn(ProductoTableModel.COL_PRECIO).setPreferredWidth(80);
            cols.getColumn(ProductoTableModel.COL_STOCK).setPreferredWidth(80);
            cols.getColumn(ProductoTableModel.COL_CAT).setPreferredWidth(120);
            cols.getColumn(ProductoTableModel.COL_FREG).setPreferredWidth(120);

            // 2. Aplicar centrado a columnas específicas
            cols.getColumn(ProductoTableModel.COL_ID).setCellRenderer(centerRenderer);
            cols.getColumn(ProductoTableModel.COL_PRECIO).setCellRenderer(centerRenderer);
            cols.getColumn(ProductoTableModel.COL_STOCK).setCellRenderer(centerRenderer);
            cols.getColumn(ProductoTableModel.COL_FREG).setCellRenderer(centerRenderer);
            // Nombre y Categoría se dejan con alineación izquierda por defecto (es más legible)

            var colAcc = cols.getColumn(ProductoTableModel.COL_ACC);
            colAcc.setMinWidth(170);
            colAcc.setPreferredWidth(190);
            colAcc.setMaxWidth(220);

            var acciones = new AccionesCell(this::editarProducto, this::eliminarProducto);
            colAcc.setCellRenderer(acciones);
            colAcc.setCellEditor(acciones);
        }

        // Orden por ID ascendente al inicio
        if (tablaProductos.getRowSorter() == null) {
            tablaProductos.setRowSorter(new TableRowSorter<>(tablaProductos.getModel()));
        }
        var sorter = (TableRowSorter<?>) tablaProductos.getRowSorter();
        List<RowSorter.SortKey> keys = new ArrayList<>();
        keys.add(new RowSorter.SortKey(ProductoTableModel.COL_ID, SortOrder.ASCENDING));
        sorter.setSortKeys(keys);
        sorter.sort();
    }

    public void recargarTabla() {
        try {
            var lista = productoService.listarProductos();
            model.setData(lista);
            // configurarTabla(); // No es necesario llamar aquí si ya se llamó en el constructor
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Error al cargar productos: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ================== ACCIONES CRUD ==================
    private void abrirDialogoAgregar() {
        Producto p = new Producto();
        boolean ok = mostrarDialogoProducto(p, true);
        if (ok) {
            try {
                productoService.agregarProducto(p);
                recargarTabla();
                JOptionPane.showMessageDialog(this, "Producto agregado.");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Validación", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error al guardar: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editarProducto(int viewRow) {
        int modelRow = tablaProductos.convertRowIndexToModel(viewRow);
        Producto p = model.getAt(modelRow);
        if (p == null) return;

        boolean ok = mostrarDialogoProducto(p, false);
        if (ok) {
            try {
                productoService.actualizarProducto(p);
                recargarTabla();
                JOptionPane.showMessageDialog(this, "Producto actualizado.");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Validación", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error al actualizar: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void eliminarProducto(int viewRow) {
        int modelRow = tablaProductos.convertRowIndexToModel(viewRow);
        Producto p = model.getAt(modelRow);
        if (p == null) return;

        int ok = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar el producto \"" + p.getNombre() + "\"?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (ok == JOptionPane.YES_OPTION) {
            try {
                productoService.eliminarProducto(p.getId());
                recargarTabla();
                JOptionPane.showMessageDialog(this, "Producto eliminado.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error al eliminar: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ================== DIÁLOGO DE PRODUCTO ==================
    private boolean mostrarDialogoProducto(Producto p, boolean esNuevo) {
        JDialog dlg = new JDialog(this, esNuevo ? "Agregar Producto" : "Editar Producto", true);
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JTextField txtNombre = new JTextField(
                p.getNombre() != null ? p.getNombre() : "", 22);
        JTextField txtCategoria = new JTextField(
                p.getCategoria() != null ? p.getCategoria() : "", 22);

        // Spinner para precio (double) y stock (int)
        // Precio: valor inicial, min, max, paso
        double precioIni = p.getPrecio() >= 0 ? p.getPrecio() : 0.0;
        int stockIni = p.getStock() >= 0 ? p.getStock() : 0;

        SpinnerNumberModel modPrecio = new SpinnerNumberModel(precioIni, 0.0, 999999.99, 0.5);
        SpinnerNumberModel modStock = new SpinnerNumberModel(stockIni, 0, 1_000_000, 1);

        JSpinner spPrecio = new JSpinner(modPrecio);
        JSpinner spStock = new JSpinner(modStock);

        JPanel content = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 10, 8, 10);
        g.fill = GridBagConstraints.HORIZONTAL;
        int row = 0;

        g.gridx = 0; g.gridy = row; g.weightx = 0;
        content.add(new JLabel("Nombre:"), g);
        g.gridx = 1; g.weightx = 1;
        content.add(txtNombre, g);

        row++;
        g.gridx = 0; g.gridy = row; g.weightx = 0;
        content.add(new JLabel("Precio:"), g);
        g.gridx = 1; g.weightx = 1;
        content.add(spPrecio, g);

        row++;
        g.gridx = 0; g.gridy = row; g.weightx = 0;
        content.add(new JLabel("Stock:"), g);
        g.gridx = 1; g.weightx = 1;
        content.add(spStock, g);

        row++;
        g.gridx = 0; g.gridy = row; g.weightx = 0;
        content.add(new JLabel("Categoría:"), g);
        g.gridx = 1; g.weightx = 1;
        content.add(txtCategoria, g);

        JButton btnCancelar = new JButton("Cancelar");
        JButton btnGuardar  = new JButton(esNuevo ? "Agregar" : "Guardar");
        JPanel pBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pBtns.add(btnCancelar);
        pBtns.add(btnGuardar);

        row++;
        g.gridx = 0; g.gridy = row; g.gridwidth = 2;
        g.weightx = 1; g.fill = GridBagConstraints.NONE;
        g.anchor = GridBagConstraints.EAST;
        content.add(pBtns, g);

        final boolean[] saved = { false };

        btnCancelar.addActionListener(e -> dlg.dispose());

        btnGuardar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String categoria = txtCategoria.getText().trim();
            double precio = ((Number) spPrecio.getValue()).doubleValue();
            int stock = ((Number) spStock.getValue()).intValue();

            // Solo llenamos el objeto; la validación la hace el servicio
            p.setNombre(nombre);
            p.setCategoria(categoria);
            p.setPrecio(precio);
            p.setStock(stock);

            saved[0] = true;
            dlg.dispose();
        });

        dlg.setContentPane(content);
        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);

        return saved[0];
    }

    // ================== CELDA ACCIONES ==================
    private static class AccionesCell extends AbstractCellEditor
            implements TableCellRenderer, TableCellEditor {

        private final JPanel panel = new JPanel(new GridBagLayout());
        private final JButton btnEdit = new JButton("Editar");
        private final JButton btnDel  = new JButton("Eliminar");
        private final java.util.function.IntConsumer onEditar;
        private final java.util.function.IntConsumer onEliminar;
        private int fila = -1;

        AccionesCell(java.util.function.IntConsumer onEditar,
                     java.util.function.IntConsumer onEliminar) {
            this.onEditar = onEditar;
            this.onEliminar = onEliminar;

            btnEdit.setMargin(new Insets(4, 10, 4, 10));
            btnDel.setMargin(new Insets(4, 10, 4, 10));
            btnEdit.setFocusable(false);
            btnDel.setFocusable(false);

            JPanel inner = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
            inner.setOpaque(false);
            inner.add(btnEdit);
            inner.add(btnDel);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.CENTER;
            panel.add(inner, gbc);

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

    // ================== MAIN DE PRUEBA ==================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            IProductoRepository repo = new ProductoRepository();
            ProductoService service = new ProductoService(repo);
            new ProductosViewForm(service).setVisible(true);
        });
    }
}