package com.veterinaria.veterinariaapp.ui;

import com.veterinaria.veterinariaapp.model.Usuario;
import com.veterinaria.veterinariaapp.service.UserService;

import javax.swing.*;
// --- ¡CORRECCIÓN AQUÍ! ---
import javax.swing.table.DefaultTableCellRenderer; // <-- Import que faltaba
import java.awt.*;
import java.util.List;

// ... (El resto del código de UsuariosPanel que te di antes es correcto) ...
public class UsuariosPanel extends JPanel {
    private final UserService service; 
    private JTable tabla;
    private UsuarioTableModel modelo;
    private JButton btnNuevo, btnEditar, btnPass, btnActivar, btnDesactivar, btnRefrescar;

    public UsuariosPanel(UserService service) {
        this.service = service; 
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        initUI();
        cargarUsuarios();
    }

     private void initUI() {
        JLabel title = new JLabel("Gestión de Usuarios");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setBorder(BorderFactory.createEmptyBorder(0,0,12,0));
        
        modelo = new UsuarioTableModel();
        tabla = new JTable(modelo);
        JScrollPane sp = new JScrollPane(tabla);

        btnNuevo = new JButton("Nuevo");
        btnEditar = new JButton("Editar");
        btnPass = new JButton("Cambiar contraseña");
        btnActivar = new JButton("Activar");
        btnDesactivar = new JButton("Desactivar");
        btnRefrescar = new JButton("Refrescar");

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        acciones.add(btnNuevo);
        acciones.add(btnEditar);
        acciones.add(btnPass);
        acciones.add(btnActivar);
        acciones.add(btnDesactivar);
        acciones.add(btnRefrescar);

        JPanel north = new JPanel(new BorderLayout());
        north.add(title, BorderLayout.NORTH);
        north.add(acciones, BorderLayout.CENTER);
        add(north, BorderLayout.NORTH);

        add(sp, BorderLayout.CENTER);

        tabla.setRowHeight(36);
        tabla.setFillsViewportHeight(true);
        tabla.setAutoCreateRowSorter(true);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer(); // <-- Ahora sí compila
        center.setHorizontalAlignment(SwingConstants.CENTER);

        SwingUtilities.invokeLater(() -> {
            var cols = tabla.getColumnModel();
            if (cols.getColumnCount() > UsuarioTableModel.COL_ACTIVO) { // <-- Ahora sí compila
                 cols.getColumn(0).setPreferredWidth(60);   
                 cols.getColumn(1).setPreferredWidth(160);  
                 cols.getColumn(2).setPreferredWidth(220);  
                 cols.getColumn(3).setPreferredWidth(140);  
                 cols.getColumn(UsuarioTableModel.COL_ACTIVO).setPreferredWidth(90); // <-- Ahora sí compila

                 cols.getColumn(0).setCellRenderer(center); 
                 cols.getColumn(UsuarioTableModel.COL_ACTIVO).setCellRenderer(center); // <-- Ahora sí compila
            } else {
                 System.err.println("Advertencia: El modelo de tabla de Usuarios no tiene suficientes columnas para configurar anchos.");
            }
        });

        btnRefrescar.addActionListener(e -> cargarUsuarios());
        btnNuevo.addActionListener(e -> onNuevo());
        btnEditar.addActionListener(e -> onEditar());
        btnPass.addActionListener(e -> onCambiarPassword());
        btnActivar.addActionListener(e -> onSetActivo(true));
        btnDesactivar.addActionListener(e -> onSetActivo(false));
    }
     
    // ... (cargarUsuarios, getSeleccionado, onNuevo, onEditar, onCambiarPassword, onSetActivo no cambian) ...
     public void cargarUsuarios() {
        try {
            List<Usuario> data = service.listarUsuarios(); 
            modelo.setData(data);
            SwingUtilities.invokeLater(() -> { /* Reconfigurar columnas si es necesario */ });
        } catch(Exception e) {
             JOptionPane.showMessageDialog(this, "Error al cargar usuarios: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
             e.printStackTrace();
        }
    }

    private Usuario getSeleccionado() {
        int row = tabla.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un usuario primero.");
            return null;
        }
        int modelRow = tabla.convertRowIndexToModel(row);
        return modelo.getAt(modelRow);
    }

    private void onNuevo() {
        Window owner = SwingUtilities.getWindowAncestor(this);
        UsuarioFormDialog dlg = new UsuarioFormDialog(owner instanceof Frame ? (Frame) owner : null, service, null);
        dlg.setVisible(true);
        if (dlg.isGuardado()) cargarUsuarios();
    }

    private void onEditar() {
        Usuario sel = getSeleccionado();
        if (sel == null) return;
        Window owner = SwingUtilities.getWindowAncestor(this);
        UsuarioFormDialog dlg = new UsuarioFormDialog(owner instanceof Frame ? (Frame) owner : null, service, sel);
        dlg.setVisible(true);
        if (dlg.isGuardado()) cargarUsuarios();
    }

    private void onCambiarPassword() {
        Usuario sel = getSeleccionado();
        if (sel == null) return;
        Window owner = SwingUtilities.getWindowAncestor(this);
        CambiarPasswordDialog dlg = new CambiarPasswordDialog(owner instanceof Frame ? (Frame) owner : null, service, sel.getId());
        dlg.setVisible(true);
    }

    private void onSetActivo(boolean activo) {
        Usuario sel = getSeleccionado();
        if (sel == null) return;
        if (sel.isActivo() == activo) {
            JOptionPane.showMessageDialog(this, "El usuario ya está " + (activo ? "ACTIVO" : "INACTIVO"));
            return;
        }
        int r = JOptionPane.showConfirmDialog(this,
                "¿Seguro que deseas " + (activo ? "activar" : "desactivar") + " al usuario " + sel.getNombre() + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            try {
                service.setActivo(sel.getId(), activo);
                cargarUsuarios();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace(); 
            }
        }
    }
}