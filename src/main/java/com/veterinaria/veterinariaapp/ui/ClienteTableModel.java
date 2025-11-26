package com.veterinaria.veterinariaapp.ui;

/**
 *
 * @author Yuriko Matsuo
 */
import com.veterinaria.veterinariaapp.model.Cliente;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ClienteTableModel extends AbstractTableModel {
    private final String[] cols = {"ID","Nombre","Apellido","Dirección","Teléfono","Acciones"};
    
    // --- ¡CORRECCIÓN AÑADIDA AQUÍ! ---
    public static final int COL_ACCIONES = 5; // <-- Esta línea faltaba
    
    private final List<Cliente> data = new ArrayList<>();

    public ClienteTableModel() {}
    public ClienteTableModel(List<Cliente> inicial) { if (inicial != null) data.addAll(inicial); }

    public void setData(List<Cliente> nuevos) {
        data.clear();
        if (nuevos != null) data.addAll(nuevos);
        fireTableDataChanged();
    }

    @Override public int getRowCount() { return data.size(); }
    @Override public int getColumnCount() { return cols.length; }
    @Override public String getColumnName(int c) { return cols[c]; }
    @Override public Class<?> getColumnClass(int c) { 
        // Corregido: La columna 5 (Acciones) no es String, es Object/JPanel
        if (c == 0) return Integer.class;
        if (c == COL_ACCIONES) return Object.class; 
        return String.class;
    }
    
    @Override public boolean isCellEditable(int r,int c){ 
        return c == COL_ACCIONES; // Usamos la constante
    }
    
    @Override public Object getValueAt(int r,int c){
        var x = data.get(r);
        return switch (c) {
            case 0 -> x.getIdCliente();
            case 1 -> x.getNombre();
            case 2 -> x.getApellido();
            case 3 -> x.getDireccion();
            case 4 -> x.getTelefono();
            case 5 -> ""; // Acciones (se maneja por el Renderer/Editor)
            default -> "";
        };
    }
    
    public Cliente getAt(int row) {
        return data.get(row);
    }
}