package com.veterinaria.veterinariaapp.service;

import com.veterinaria.veterinariaapp.model.Usuario;
import com.veterinaria.veterinariaapp.repository.UsuarioRepository;

public class AuthService {
    private final UsuarioRepository repo = new UsuarioRepository();

    public Usuario autenticar(String email, String password) {
        var u = repo.findByEmail(email);
        if (u == null)              throw new IllegalArgumentException("Usuario no encontrado.");
        if (!u.isActivo())          throw new IllegalStateException("Usuario inactivo.");
        if (!password.equals(u.getPassHash()))
            throw new IllegalArgumentException("Credenciales inválidas.");
        return u;
    }
}
