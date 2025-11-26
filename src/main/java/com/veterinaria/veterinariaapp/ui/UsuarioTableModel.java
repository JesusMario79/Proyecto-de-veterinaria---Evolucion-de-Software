package com.veterinaria.veterinariaapp.ui;

import com.veterinaria.veterinariaapp.model.Usuario;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class UsuarioTableModel extends AbstractTableModel {
    private final String[] columnas = {"ID", "Nombre", "Email", "Rol", "Activo"};
    
    // --- ¡CORRECCIÓN AÑADIDA AQUÍ! ---
    public static final int COL_ACTIVO = 4; // <-- Esta línea faltaba
    
    private List<Usuario> data = new ArrayList<>();

    public void setData(List<Usuario> usuarios) {
        this.data = usuarios != null ? usuarios : new ArrayList<>();
        fireTableDataChanged();
    }

    public Usuario getAt(int fila) { return data.get(fila); }

    @Override public int getRowCount() { return data.size(); }
    @Override public int getColumnCount() { return columnas.length; }
    @Override public String getColumnName(int col) { return columnas[col]; }
    
    // Añadimos getColumnClass para que el sorter funcione mejor
    @Override public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0) return Integer.class; // ID
        if (columnIndex == COL_ACTIVO) return String.class; // Activo (Sí/No)
        return String.class; // El resto son Strings
    }

    @Override
    public Object getValueAt(int fila, int col) {
        Usuario u = data.get(fila);
        return switch (col) {
            case 0 -> u.getId();
            case 1 -> u.getNombre();
            case 2 -> u.getEmail();
            case 3 -> u.getRol(); // Asumiendo que Usuario tiene getRol() que devuelve el nombre
            case COL_ACTIVO -> u.isActivo() ? "Sí" : "No"; // Usamos la constante
            default -> "";
        };
    }
}