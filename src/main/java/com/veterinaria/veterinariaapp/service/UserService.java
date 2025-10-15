package com.veterinaria.veterinariaapp.service;

import com.veterinaria.veterinariaapp.model.Usuario;
import com.veterinaria.veterinariaapp.repository.UsuarioRepository;
import com.veterinaria.veterinariaapp.util.PasswordHasher;

import java.util.List;
import java.util.Map;

public class UserService {
    private final UsuarioRepository repo = new UsuarioRepository();

    public List<Usuario> listar(){ return repo.findAll(); }
    public Map<Integer,String> roles(){ return repo.findAllRoles(); }
    public Usuario porId(int id){ return repo.findById(id); }

    public int crear(String nombre, String email, String passPlano, int rolId, boolean activo){
        if (nombre==null || nombre.isBlank()) throw new IllegalArgumentException("Nombre requerido");
        if (email==null  || email.isBlank())  throw new IllegalArgumentException("Email requerido");
        if (passPlano==null || passPlano.length()<6) throw new IllegalArgumentException("Contraseña mínima 6");

        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setEmail(email);
        u.setPassHash(PasswordHasher.hash(passPlano));
        u.setRolId(rolId);
        u.setActivo(activo);
        return repo.insert(u);
    }

    public void editar(int id, String nombre, String email, int rolId, boolean activo){
        if (id<=0) throw new IllegalArgumentException("ID inválido");
        Usuario u = new Usuario();
        u.setId(id); u.setNombre(nombre); u.setEmail(email); u.setRolId(rolId); u.setActivo(activo);
        repo.update(u);
    }

    public void cambiarPassword(int id, String nueva){
        if(nueva==null || nueva.length()<6) throw new IllegalArgumentException("Contraseña mínima 6");
        repo.updatePassword(id, PasswordHasher.hash(nueva));
    }

    public void setActivo(int id, boolean activo){ repo.setActivo(id, activo); }
}
