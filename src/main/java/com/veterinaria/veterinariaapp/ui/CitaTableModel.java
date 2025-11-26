package com.veterinaria.veterinariaapp.ui;

import com.veterinaria.veterinariaapp.model.Cita;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * TableModel para mostrar objetos Cita en una JTable.
 */
public class CitaTableModel extends AbstractTableModel {

    // Definimos las columnas que mostraremos en la tabla de citas.
    private final String[] cols = {
        "ID", "Fecha y Hora", "Mascota", "Cliente", "Motivo", "Estado", "Acciones"
    };
    
    // Definimos los tipos de datos de cada columna para un renderizado correcto.
    private final Class<?>[] types = {
        Integer.class, String.class, String.class, String.class, String.class, String.class, Object.class
    };

    // Constante para identificar fácilmente la columna de acciones.
    public static final int COL_ACCIONES = 6;

    private List<Cita> data;
    
    // Formateador para mostrar la fecha y hora de una manera más amigable.
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public CitaTableModel(List<Cita> data) {
        this.data = data;
    }

    /**
     * Actualiza los datos del modelo y notifica a la tabla para que se redibuje.
     * @param d La nueva lista de citas.
     */
    public void setData(List<Cita> d) {
        this.data = d;
        fireTableDataChanged();
    }

    /**
     * Devuelve el objeto Cita de una fila específica.
     * @param row El índice de la fila.
     * @return El objeto Cita correspondiente.
     */
    public Cita getAt(int row) {
        return data.get(row);
    }

    @Override
    public int getRowCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public int getColumnCount() {
        return cols.length;
    }

    @Override
    public String getColumnName(int c) {
        return cols[c];
    }

    @Override
    public Class<?> getColumnClass(int c) {
        return types[c];
    }

    @Override
    public boolean isCellEditable(int r, int c) {
        // Solo la columna de "Acciones" será editable para que funcionen los botones.
        return c == COL_ACCIONES;
    }

    @Override
    public Object getValueAt(int r, int c) {
        Cita cita = data.get(r);
        
        return switch (c) {
            case 0 -> cita.getId();
            case 1 -> cita.getFechaHora() == null ? "" : cita.getFechaHora().format(FORMATTER);
            case 2 -> cita.getMascotaNombre();
            case 3 -> cita.getClienteNombre();
            case 4 -> cita.getMotivo();
            case 5 -> cita.getEstado().toString(); // Usamos el toString() del Enum
            case 6 -> null; // La columna de acciones no tiene un valor, se renderiza aparte.
            default -> null;
        };
    }
}