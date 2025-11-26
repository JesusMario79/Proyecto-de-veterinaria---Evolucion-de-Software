package com.veterinaria.veterinariaapp.ui;

import com.veterinaria.veterinariaapp.model.Usuario;
import com.veterinaria.veterinariaapp.service.UserService;
import com.veterinaria.veterinariaapp.repository.IUsuarioRepository;
import com.veterinaria.veterinariaapp.repository.UsuarioRepository;

import javax.swing.*;
// --- ¡CORRECCIÓN AQUÍ! ---
import javax.swing.table.DefaultTableCellRenderer; // <-- Import que faltaba
import java.awt.*;
import java.util.List;

// ... (El resto del código de UsuariosFrame que te di antes es correcto) ...
public class UsuariosFrame extends JFrame {
    private final UserService service; 
    private JTable tabla;
    private UsuarioTableModel modelo;
    private JButton btnNuevo, btnEditar, btnPass, btnActivar, btnDesactivar, btnRefrescar;

    public UsuariosFrame(UserService service) {
        this.service = service; 
        setTitle("Gestión de Usuarios");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setSize(900, 520);
        setLocationRelativeTo(null);
        initUI();
        cargarUsuarios();
    }
    
     private void initUI() {
        modelo = new UsuarioTableModel();
        tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
        add(acciones, BorderLayout.NORTH);
        add(sp, BorderLayout.CENTER);
        
        tabla.setRowHeight(36);
        tabla.setFillsViewportHeight(true);
        tabla.setAutoCreateRowSorter(true);
        tabla.getTableHeader().setReorderingAllowed(false);
        DefaultTableCellRenderer center = new DefaultTableCellRenderer(); // <-- Ahora sí compila
        center.setHorizontalAlignment(SwingConstants.CENTER);
        SwingUtilities.invokeLater(() -> {
            var cols = tabla.getColumnModel();
            if (cols.getColumnCount() > UsuarioTableModel.COL_ACTIVO) { // <-- Ahora sí compila
                 cols.getColumn(0).setPreferredWidth(60);   
                 /* ... otros anchos ... */
                 cols.getColumn(UsuarioTableModel.COL_ACTIVO).setCellRenderer(center); // <-- Ahora sí compila
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
     private void cargarUsuarios() {
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
        UsuarioFormDialog dlg = new UsuarioFormDialog(this, service, null);
        dlg.setVisible(true);
        if (dlg.isGuardado()) cargarUsuarios();
    }

    private void onEditar() {
        Usuario sel = getSeleccionado();
        if (sel == null) return;
        UsuarioFormDialog dlg = new UsuarioFormDialog(this, service, sel);
        dlg.setVisible(true);
        if (dlg.isGuardado()) cargarUsuarios();
    }

    private void onCambiarPassword() {
        Usuario sel = getSeleccionado();
        if (sel == null) return;
        CambiarPasswordDialog dlg = new CambiarPasswordDialog(this, service, sel.getId());
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

    // --- ¡MAIN DE PRUEBA CORREGIDO! ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // --- ¡ENSAMBLAJE DE PRUEBA! ---
            IUsuarioRepository repo = new UsuarioRepository();
            UserService service = new UserService(repo);
            new UsuariosFrame(service).setVisible(true); // <-- Inyecta el servicio
        });
    }
}