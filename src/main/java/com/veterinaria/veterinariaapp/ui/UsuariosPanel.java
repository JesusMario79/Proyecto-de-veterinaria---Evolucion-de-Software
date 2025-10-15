package com.veterinaria.veterinariaapp.ui;

import com.veterinaria.veterinariaapp.model.Usuario;
import com.veterinaria.veterinariaapp.service.UserService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class UsuariosPanel extends JPanel {
    private final UserService service = new UserService();
    private JTable tabla;
    private UsuarioTableModel modelo;

    private JButton btnNuevo, btnEditar, btnPass, btnActivar, btnDesactivar, btnRefrescar;

    public UsuariosPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        initUI();
        cargarUsuarios();
    }

    private void initUI() {
        JLabel title = new JLabel("Gestión de Usuarios");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setBorder(BorderFactory.createEmptyBorder(0,0,12,0));
        add(title, BorderLayout.NORTH);

        modelo = new UsuarioTableModel();
        tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane sp = new JScrollPane(tabla);

        // Botones
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

        JPanel center = new JPanel(new BorderLayout());
        center.add(acciones, BorderLayout.NORTH);
        center.add(sp, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);

        // Eventos de botones
        btnRefrescar.addActionListener(e -> cargarUsuarios());
        btnNuevo.addActionListener(e -> onNuevo());
        btnEditar.addActionListener(e -> onEditar());
        btnPass.addActionListener(e -> onCambiarPassword());
        btnActivar.addActionListener(e -> onSetActivo(true));
        btnDesactivar.addActionListener(e -> onSetActivo(false));
    }

    private void cargarUsuarios() {
        List<Usuario> data = service.listar();
        modelo.setData(data);
    }

    private Usuario getSeleccionado() {
        int row = tabla.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un usuario primero.");
            return null;
        }
        return modelo.getAt(row);
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
            }
        }
    }
}
