package com.veterinaria.veterinariaapp.ui;

import com.veterinaria.veterinariaapp.service.UserService;

import javax.swing.*;
import java.awt.*;

public class CambiarPasswordDialog extends JDialog {
    private final UserService service;
    private final int userId;
    private JPasswordField txtNueva, txtRepetir;

    public CambiarPasswordDialog(Frame parent, UserService service, int userId) {
        super(parent, true);
        this.service = service;
        this.userId = userId;

        setTitle("Cambiar contraseña");
        setSize(350, 200);
        setLocationRelativeTo(parent);

        initUI();
    }

    private void initUI() {
        JPanel form = new JPanel(new GridLayout(2,2,8,8));
        txtNueva = new JPasswordField();
        txtRepetir = new JPasswordField();
        form.add(new JLabel("Nueva contraseña:")); form.add(txtNueva);
        form.add(new JLabel("Repetir contraseña:")); form.add(txtRepetir);

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

    private void onGuardar() {
        String p1 = new String(txtNueva.getPassword());
        String p2 = new String(txtRepetir.getPassword());
        if (p1.length() < 6) {
            JOptionPane.showMessageDialog(this, "Debe tener al menos 6 caracteres.");
            return;
        }
        if (!p1.equals(p2)) {
            JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden.");
            return;
        }
        try {
            service.cambiarPassword(userId, p1);
            JOptionPane.showMessageDialog(this, "Contraseña actualizada.");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
