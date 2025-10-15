package com.veterinaria.veterinariaapp.service;

import com.veterinaria.veterinariaapp.model.Usuario;
import com.veterinaria.veterinariaapp.repository.UsuarioRepository;
import com.veterinaria.veterinariaapp.util.PasswordHasher;

public class AuthService {
    private final UsuarioRepository repo = new UsuarioRepository();

    public Usuario autenticar(String email, String password) {
        var u = repo.findByEmail(email);
        if (u == null) throw new IllegalArgumentException("Usuario no encontrado.");
        if (!u.isActivo()) throw new IllegalStateException("Usuario inactivo.");
        if (!PasswordHasher.verify(password, u.getPassHash()))   // <-- AQUÍ
            throw new IllegalArgumentException("Credenciales inválidas.");
        return u;
    }
}