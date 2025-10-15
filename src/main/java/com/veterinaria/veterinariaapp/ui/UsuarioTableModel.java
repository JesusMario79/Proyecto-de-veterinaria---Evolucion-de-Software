package com.veterinaria.veterinariaapp.ui;

import com.veterinaria.veterinariaapp.model.Usuario;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class UsuarioTableModel extends AbstractTableModel {
    private final String[] columnas = {"ID", "Nombre", "Email", "Rol", "Activo"};
    private List<Usuario> data = new ArrayList<>();

    public void setData(List<Usuario> usuarios) {
        this.data = usuarios != null ? usuarios : new ArrayList<>();
        fireTableDataChanged();
    }

    public Usuario getAt(int fila) { return data.get(fila); }

    @Override public int getRowCount() { return data.size(); }
    @Override public int getColumnCount() { return columnas.length; }
    @Override public String getColumnName(int col) { return columnas[col]; }

    @Override
    public Object getValueAt(int fila, int col) {
        Usuario u = data.get(fila);
        return switch (col) {
            case 0 -> u.getId();
            case 1 -> u.getNombre();
            case 2 -> u.getEmail();
            case 3 -> u.getRol();
            case 4 -> u.isActivo() ? "Sí" : "No";
            default -> "";
        };
    }
}
