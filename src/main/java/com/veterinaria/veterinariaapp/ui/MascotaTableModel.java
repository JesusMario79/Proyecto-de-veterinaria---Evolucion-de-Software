package com.veterinaria.veterinariaapp.ui;

import com.veterinaria.veterinariaapp.model.Mascota;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.List;

public class MascotaTableModel extends AbstractTableModel {
    private final String[] cols = {"ID","Imagen","Nombre","Fecha Reg.","Fecha Nac.","Raza","Especie","Cliente","Acciones"};
    private final Class<?>[] types = {Integer.class, ImageIcon.class, String.class, String.class, String.class, String.class, String.class, String.class, Object.class};
// para ubicar la columna Acciones sin “magia”
    public static final int COL_ACC = 8;
    
    private List<Mascota> data;

    public MascotaTableModel(List<Mascota> data){ this.data = data; }
    public void setData(List<Mascota> d){ this.data = d; fireTableDataChanged(); }
    public Mascota getAt(int row){ return data.get(row); }

    @Override public int getRowCount(){ return data==null?0:data.size(); }
    @Override public int getColumnCount(){ return cols.length; }
    @Override public String getColumnName(int c){ return cols[c]; }
    @Override public Class<?> getColumnClass(int c){ return types[c]; }
    @Override public boolean isCellEditable(int r,int c){ 
        return c == COL_ACC;  // solo Acciones
    }

    @Override public Object getValueAt(int r, int c) {
        Mascota m = data.get(r);
        return switch (c) {
            case 0 -> m.getId();
            case 1 -> icon(m.getFoto());
            case 2 -> m.getNombre();
            case 3 -> m.getFechaRegistro()==null? "" : m.getFechaRegistro().toString();
            case 4 -> m.getFechaNacimiento()==null? "" : m.getFechaNacimiento().toString();
            case 5 -> m.getRaza();
            case 6 -> m.getEspecie();
            case 7 -> m.getClienteNombre();
            case 8 -> null; // Acciones no muestra valor, muestra botones vía editor/renderer
            default -> null;
        };
    }
    
    private ImageIcon icon(byte[] b){
        if (b==null || b.length==0) return null;
        Image img = new ImageIcon(b).getImage().getScaledInstance(40,40,Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
}
