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
        // Ajustamos tamaño un poco para que quepan mejor los labels largos
        setSize(450, 320); 
        setLocationRelativeTo(parent);

        initUI();
        cargarRoles();
        if (usuario != null) cargarDatos();
    }

    private void initUI() {
        // Usamos GridBagLayout para mejor control
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        txtNombre = new JTextField(20); // Tamaño inicial
        txtEmail = new JTextField(20);
        txtPass = new JPasswordField(20);
        chkActivo = new JCheckBox("Activo", true);
        cboRol = new JComboBox<>();

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; form.add(txtNombre, gbc);
        
        row++; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE; // Reset fill and weight
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; form.add(txtEmail, gbc);
        
        row++; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        // Label más claro para la contraseña
        JLabel lblPass = new JLabel(usuario == null ? "Contraseña:" : "Contraseña (solo al crear):");
        gbc.gridx = 0; gbc.gridy = row; form.add(lblPass, gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; form.add(txtPass, gbc);

        row++; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Rol:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; form.add(cboRol, gbc);

        row++; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Estado:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.anchor = GridBagConstraints.WEST; form.add(chkActivo, gbc);

        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        btnGuardar.addActionListener(e -> onGuardar());
        btnCancelar.addActionListener(e -> dispose());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnGuardar);
        botones.add(btnCancelar);

        add(form, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
        
        // Deshabilitar campo de contraseña si estamos editando
        if(usuario != null) {
            txtPass.setEnabled(false);
        }
    }

    private void cargarRoles() {
        try {
            // --- ¡CORREGIDO! ---
            roles = service.listarRoles(); // Llama al nuevo método
            cboRol.removeAllItems(); // Limpia por si acaso
            for (String r : roles.values()) cboRol.addItem(r);
            
            // Seleccionar el rol inicial (si hay roles)
            if (!roles.isEmpty() && usuario == null) { // Solo si es nuevo
                 cboRol.setSelectedIndex(0);
                 rolSeleccionado = roles.keySet().iterator().next();
            } else if (usuario != null && usuario.getRolId() != 0) { // Si edita y tiene rol
                 rolSeleccionado = usuario.getRolId();
                 String rolNombre = roles.get(rolSeleccionado);
                 if (rolNombre != null) {
                      cboRol.setSelectedItem(rolNombre);
                 } else { // Si el rol del usuario ya no existe, selecciona el primero
                      cboRol.setSelectedIndex(0);
                      rolSeleccionado = roles.keySet().iterator().next();
                 }
            } else if (!roles.isEmpty()){ // Si edita pero no tenía rol (raro), selecciona el primero
                 cboRol.setSelectedIndex(0);
                 rolSeleccionado = roles.keySet().iterator().next();
            }

            // Listener para actualizar rolSeleccionado cuando el usuario cambia la selección
            cboRol.addActionListener(e -> {
                String sel = (String) cboRol.getSelectedItem();
                rolSeleccionado = roles.entrySet().stream()
                        .filter(x -> x.getValue().equals(sel))
                        .map(Map.Entry::getKey)
                        .findFirst().orElse(null);
            });
        } catch (Exception e) {
             JOptionPane.showMessageDialog(this, "Error al cargar roles: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
             e.printStackTrace();
             roles = new LinkedHashMap<>(); // Asegura que 'roles' no sea null
        }
    }

    private void cargarDatos() {
        txtNombre.setText(usuario.getNombre());
        txtEmail.setText(usuario.getEmail());
        chkActivo.setSelected(usuario.isActivo());
        // La selección del rol ya se maneja en cargarRoles()
        // txtPass se deja vacío y deshabilitado
    }

    private void onGuardar() {
        try {
            String nombre = txtNombre.getText().trim();
            String email = txtEmail.getText().trim();
            boolean activo = chkActivo.isSelected();
            String pass = new String(txtPass.getPassword()); // Leemos aunque esté disabled

            if (rolSeleccionado == null) {
                // Verificamos si hay roles cargados. Si no, es un error mayor.
                if (roles.isEmpty()){
                     throw new IllegalStateException("No hay roles disponibles. Verifica la conexión o la tabla 'roles'.");
                } else {
                     throw new IllegalArgumentException("Selecciona un rol.");
                }
            }

            if (usuario == null) { // --- CREANDO ---
                 // --- ¡CORREGIDO! ---
                service.crearUsuario(nombre, email, pass, rolSeleccionado, activo); // Llama al nuevo método
                JOptionPane.showMessageDialog(this, "Usuario creado.");
            } else { // --- EDITANDO ---
                 // --- ¡CORREGIDO! ---
                service.editarUsuario(usuario.getId(), nombre, email, rolSeleccionado, activo); // Llama al nuevo método
                JOptionPane.showMessageDialog(this, "Usuario actualizado.");
            }
            guardado = true;
            dispose();
        } catch (IllegalArgumentException | IllegalStateException ex) { // Captura errores de validación o estado
             JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Datos", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) { // Captura otros errores (BD, etc.)
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public boolean isGuardado() { return guardado; }
}