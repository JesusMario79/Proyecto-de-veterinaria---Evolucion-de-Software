package com.veterinaria.veterinariaapp.ui;

import com.veterinaria.veterinariaapp.model.Servicios;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

/**
 *
 * @author Yuriko Matsuo
 */
public class ServiciosTableModel extends AbstractTableModel {

    // Cambiado: Ahora tiene 5 columnas, incluyendo "Acciones"
    private final String[] cols = {"ID", "Nombre", "Precio", "Descripción", "Acciones"};
    
    // Nueva constante para la columna de Acciones (índice 4)
    public static final int COL_ACCIONES = 4;
    
    private final List<Servicios> data = new ArrayList<>();

    // Constructores
    public ServiciosTableModel() {}
    public ServiciosTableModel(List<Servicios> inicial) { 
        if (inicial != null) data.addAll(inicial); 
    }

    // --- Métodos de Gestión de Datos ---

    public void setData(List<Servicios> nuevos) {
        data.clear();
        if (nuevos != null) data.addAll(nuevos);
        fireTableDataChanged();
    }
    
    public Servicios getAt(int row) {
        // Devuelve el objeto Servicios en la fila especificada
        return data.get(row);
    }

    // --- Métodos del AbstractTableModel ---

    @Override 
    public int getRowCount() { 
        return data.size(); 
    }
    
    @Override 
    public int getColumnCount() { 
        // 5 columnas ahora
        return cols.length; 
    }
    
    @Override 
    public String getColumnName(int c) { 
        return cols[c]; 
    }
    
    @Override 
    public Class<?> getColumnClass(int c) { 
        // Define el tipo de dato para la columna
        return switch (c) {
            case 0 -> Integer.class;       // ID
            case 1 -> String.class;        // Nombre
            case 2 -> BigDecimal.class;    // Precio
            case COL_ACCIONES -> Object.class; // <--- OBJETO para el Renderer/Editor
            default -> String.class;       // Descripción
        };
    }
    
    @Override 
    public boolean isCellEditable(int r, int c){ 
        // SOLO la columna de acciones es editable
        return c == COL_ACCIONES; 
    }
    
    @Override 
    public Object getValueAt(int r, int c){
        var x = data.get(r);
        return switch (c) {
            case 0 -> x.getId();
            case 1 -> x.getNombre();
            case 2 -> x.getPrecio();
            case 3 -> x.getDescripcion();
            case COL_ACCIONES -> ""; // Retornamos cadena vacía; el Renderer pintará los botones
            default -> "";
        };
    }
}