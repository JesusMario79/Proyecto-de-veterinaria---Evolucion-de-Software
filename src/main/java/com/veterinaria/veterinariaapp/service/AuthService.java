package com.veterinaria.veterinariaapp.service;

import com.veterinaria.veterinariaapp.model.Usuario;
// --- ¡CAMBIOS EN IMPORTS! ---
// import com.veterinaria.veterinariaapp.repository.UsuarioRepository; // <-- ELIMINADO
import com.veterinaria.veterinariaapp.repository.IUsuarioRepository; // <-- AÑADIDO
import com.veterinaria.veterinariaapp.util.PasswordHasher;

public class AuthService {
    // --- ¡CORREGIDO! ---
    // private final UsuarioRepository repo = new UsuarioRepository(); // <-- ELIMINADO
    private final IUsuarioRepository repo; // <-- AÑADIDO (Depende de la interfaz)

    // Constructor para Inyección de Dependencia
    public AuthService(IUsuarioRepository repo) {
        this.repo = repo;
    }

    /**
     * Autentica un usuario por email y contraseña.
     * @param email El email del usuario.
     * @param password La contraseña en texto plano.
     * @return El objeto Usuario si la autenticación es exitosa.
     * @throws IllegalArgumentException Si el usuario no existe o las credenciales son inválidas.
     * @throws IllegalStateException Si el usuario está inactivo.
     * @throws Exception Si ocurre un error de base de datos.
     */
    public Usuario autenticar(String email, String password) throws Exception {
        // Llama a findByEmail de la interfaz
        var u = repo.findByEmail(email);

        if (u == null) {
            throw new IllegalArgumentException("Usuario o contraseña incorrectos."); // Mensaje más genérico por seguridad
        }
        if (!u.isActivo()) {
            throw new IllegalStateException("La cuenta de usuario está inactiva.");
        }
        if (!PasswordHasher.verify(password, u.getPassHash())) {
             throw new IllegalArgumentException("Usuario o contraseña incorrectos."); // Mensaje más genérico
        }
        // Autenticación exitosa
        return u;
    }
}