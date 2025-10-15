package com.veterinaria.veterinariaapp.ui;

import com.veterinaria.veterinariaapp.model.Usuario;
import com.veterinaria.veterinariaapp.service.UserService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * JFrame manual (no generado por el diseñador) para gestionar usuarios.
 * Muestra una tabla con botones de acciones: Nuevo, Editar, Cambiar Password, Activar/Desactivar, Refrescar.
 */
public class UsuariosFrame extends JFrame {

    private final UserService service = new UserService();
    private JTable tabla;
    private UsuarioTableModel modelo;

    private JButton btnNuevo, btnEditar, btnPass, btnActivar, btnDesactivar, btnRefrescar;

    public UsuariosFrame() {
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

        // Botones
        btnNuevo = new JButton("Nuevo");
        btnEditar = new JButton("Editar");
        btnPass = new JButton("Cambiar contraseña");
        btnActivar = new JButton("Activar");
        btnDesactivar = new JButton("Desactivar");
        btnRefrescar = new JButton("Refrescar");

        // Panel de acciones
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        acciones.add(btnNuevo);
        acciones.add(btnEditar);
        acciones.add(btnPass);
        acciones.add(btnActivar);
        acciones.add(btnDesactivar);
        acciones.add(btnRefrescar);

        // Layout principal
        add(acciones, BorderLayout.NORTH);
        add(sp, BorderLayout.CENTER);

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
            }
        }
    }

    // Prueba rápida independiente (puedes ejecutar Shift+F6 en NetBeans)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UsuariosFrame().setVisible(true));
    }
}
