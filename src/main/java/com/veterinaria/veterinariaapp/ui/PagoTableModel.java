package com.veterinaria.veterinariaapp.ui;

import com.veterinaria.veterinariaapp.model.Pago;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class PagoTableModel extends AbstractTableModel {
    private final String[] columnas = {"ID", "ID Cita", "Monto", "Método", "Fecha"};
    private List<Pago> datos = new ArrayList<>();

    public void setDatos(List<Pago> datos) {
        this.datos = datos;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() { return datos.size(); }

    @Override
    public int getColumnCount() { return columnas.length; }

    @Override
    public String getColumnName(int column) { return columnas[column]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Pago p = datos.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> p.getId();
            case 1 -> p.getCitaId();
            case 2 -> p.getMonto();
            case 3 -> p.getMetodoPago();
            case 4 -> p.getFecha();
            default -> null;
        };
    }
}