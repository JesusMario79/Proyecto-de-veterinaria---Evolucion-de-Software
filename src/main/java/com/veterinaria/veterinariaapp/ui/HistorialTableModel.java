package com.veterinaria.veterinariaapp.ui;

import com.veterinaria.veterinariaapp.model.HistorialMedico;
import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HistorialTableModel extends AbstractTableModel {
    private final String[] cols = {"Fecha", "Diagnóstico", "Tratamiento"};
    private List<HistorialMedico> data;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public HistorialTableModel(List<HistorialMedico> data) { this.data = data; }

    public void setData(List<HistorialMedico> data) {
        this.data = data;
        fireTableDataChanged();
    }

    @Override public int getRowCount() { return data == null ? 0 : data.size(); }
    @Override public int getColumnCount() { return cols.length; }
    @Override public String getColumnName(int c) { return cols[c]; }
    @Override public Object getValueAt(int r, int c) {
        HistorialMedico h = data.get(r);
        return switch(c) {
            case 0 -> h.getFecha().format(FMT);
            case 1 -> h.getDescripcion();
            case 2 -> h.getTratamiento();
            default -> null;
        };
    }
}