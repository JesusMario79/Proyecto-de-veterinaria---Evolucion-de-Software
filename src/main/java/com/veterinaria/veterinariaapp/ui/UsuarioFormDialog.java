package com.veterinaria.veterinariaapp.ui;

import com.veterinaria.veterinariaapp.model.Usuario;
import com.veterinaria.veterinariaapp.service.UserService;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class UsuarioFormDialog extends JDialog {
    private final UserService service;
    private final Usuario usuario; // null = crear, !=null = editar
    private boolean guardado = false;

    private JTextField txtNombre, txtEmail;
    private JPasswordField txtPass;
    private JCheckBox chkActivo;
    private JComboBox<String> cboRol;
    private Map<Integer, String> roles = new LinkedHashMap<>();
    private Integer rolSeleccionado;

    public UsuarioFormDialog(Frame parent, UserService service, Usuario usuario) {
        super(parent, true);
        this.service = service;
        this.usuario = usuario;

        setTitle(usuario == null ? "Nuevo usuario" : "Editar usuario");
        setSize(400, 300);
        setLocationRelativeTo(parent);

        initUI();
        cargarRoles();
        if (usuario != null) cargarDatos();
    }

    private void initUI() {
        JPanel form = new JPanel(new GridLayout(5,2,8,8));
        txtNombre = new JTextField();
        txtEmail = new JTextField();
        txtPass = new JPasswordField();
        chkActivo = new JCheckBox("Activo", true);
        cboRol = new JComboBox<>();

        form.add(new JLabel("  Nombre:")); form.add(txtNombre);
        form.add(new JLabel("  Email:")); form.add(txtEmail);
        form.add(new JLabel(usuario == null ? "  Contraseña:" : "  Contraseña (solo al crear):"));
        form.add(txtPass);
        form.add(new JLabel("  Rol:")); form.add(cboRol);
        form.add(new JLabel("  Estado:")); form.add(chkActivo);

        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        btnGuardar.addActionListener(e -> onGuardar());
        btnCancelar.addActionListener(e -> dispose());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnGuardar);
        botones.add(btnCancelar);

        add(form, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
    }

    private void cargarRoles() {
        roles = service.roles();
        for (String r : roles.values()) cboRol.addItem(r);
        if (!roles.isEmpty()) {
            cboRol.setSelectedIndex(0);
            rolSeleccionado = roles.keySet().iterator().next();
        }
        cboRol.addActionListener(e -> {
            String sel = (String) cboRol.getSelectedItem();
            rolSeleccionado = roles.entrySet().stream()
                    .filter(x -> x.getValue().equals(sel))
                    .map(Map.Entry::getKey)
                    .findFirst().orElse(null);
        });
    }

    private void cargarDatos() {
        txtNombre.setText(usuario.getNombre());
        txtEmail.setText(usuario.getEmail());
        chkActivo.setSelected(usuario.isActivo());
        if (usuario.getRol() != null) cboRol.setSelectedItem(usuario.getRol());
        txtPass.setEnabled(false);
    }

    private void onGuardar() {
        try {
            String nombre = txtNombre.getText().trim();
            String email = txtEmail.getText().trim();
            boolean activo = chkActivo.isSelected();
            String pass = new String(txtPass.getPassword());

            if (rolSeleccionado == null) throw new IllegalArgumentException("Selecciona un rol.");

            if (usuario == null) {
                service.crear(nombre, email, pass, rolSeleccionado, activo);
                JOptionPane.showMessageDialog(this, "Usuario creado.");
            } else {
                service.editar(usuario.getId(), nombre, email, rolSeleccionado, activo);
                JOptionPane.showMessageDialog(this, "Usuario actualizado.");
            }
            guardado = true;
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isGuardado() { return guardado; }
}
