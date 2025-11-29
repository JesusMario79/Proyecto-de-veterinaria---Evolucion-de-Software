package com.veterinaria.veterinariaapp.ui;

import com.veterinaria.veterinariaapp.model.Producto;
import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal; // Asumiendo que usas BigDecimal para precio
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ProductoTableModel extends AbstractTableModel {

    // Definición de columnas
    private final String[] columnas = {
            "ID",
            "Nombre",
            "Precio",
            "Stock",
            "Categoría",
            "Fecha registro",
            "Acciones"
    };

    // Índices constantes para evitar "números mágicos"
    public static final int COL_ID     = 0;
    public static final int COL_NOMBRE = 1;
    public static final int COL_PRECIO = 2;
    public static final int COL_STOCK  = 3;
    public static final int COL_CAT    = 4;
    public static final int COL_FREG   = 5;
    public static final int COL_ACC    = 6;

    private List<Producto> data = new ArrayList<>();
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void setData(List<Producto> productos) {
        this.data = (productos != null) ? productos : new ArrayList<>();
        fireTableDataChanged();
    }

    public Producto getAt(int fila) {
        if (fila >= 0 && fila < data.size()) {
            return data.get(fila);
        }
        return null;
    }

    @Override
    public int getRowCount() { return data.size(); }

    @Override
    public int getColumnCount() { return columnas.length; }

    @Override
    public String getColumnName(int col) { return columnas[col]; }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case COL_ID     -> Integer.class;
            // Usamos Object o Number para precio para que soporte BigDecimal o Double
            case COL_PRECIO -> Double.class; 
            case COL_STOCK  -> Integer.class;
            // La columna de acciones suele ser un JPanel (Object)
            case COL_ACC    -> Object.class; 
            default         -> String.class;
        };
    }

    @Override
    public Object getValueAt(int fila, int col) {
        Producto p = data.get(fila);
        return switch (col) {
            case COL_ID     -> p.getId();
            case COL_NOMBRE -> p.getNombre();
            case COL_PRECIO -> p.getPrecio();
            case COL_STOCK  -> p.getStock();
            case COL_CAT    -> p.getCategoria();
            case COL_FREG   -> {
                if (p.getCreatedAt() == null) yield "";
                // Manejo seguro de fechas (dependiendo si usas Timestamp o LocalDateTime)
                try {
                    yield p.getCreatedAt().toLocalDateTime().toLocalDate().format(fmt);
                } catch (Exception e) {
                    yield p.getCreatedAt().toString(); // Fallback
                }
            }
            case COL_ACC    -> "Editar / Eliminar"; // El Renderer se encargará de poner los botones
            default         -> "";
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // Solo permitimos editar la columna de botones para que detecte los clics
        return columnIndex == COL_ACC;
    }
}