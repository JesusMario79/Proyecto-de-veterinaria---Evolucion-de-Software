package com.veterinaria.veterinariaapp.service;

import com.veterinaria.veterinariaapp.model.Usuario;
// --- ¡CAMBIOS EN IMPORTS! ---
// import com.veterinaria.veterinariaapp.repository.UsuarioRepository; // <-- ELIMINADO
import com.veterinaria.veterinariaapp.repository.IUsuarioRepository; // <-- AÑADIDO
import com.veterinaria.veterinariaapp.util.PasswordHasher;

import java.util.List;
import java.util.Map;

public class UserService {
    
    // --- ¡CORREGIDO! ---
    // private final UsuarioRepository repo = new UsuarioRepository(); // <-- ELIMINADO
    private final IUsuarioRepository repo; // <-- AÑADIDO (Depende de la interfaz)

    // Constructor para Inyección de Dependencia (DIP)
    public UserService(IUsuarioRepository repo) {
        this.repo = repo;
    }

    // --- Métodos de Lógica de Negocio (SRP) ---

    public List<Usuario> listarUsuarios(){ 
        return repo.findAll(); 
    }
    
    public Map<Integer,String> listarRoles(){ 
        return repo.findAllRoles(); 
    }
    
    public Usuario buscarPorId(int id){ 
        return repo.findById(id); 
    }

    public int crearUsuario(String nombre, String email, String passPlano, int rolId, boolean activo) 
            throws IllegalArgumentException {
        
        // --- Lógica de Negocio (Validación) ---
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("Nombre requerido");
        }
        if (email == null  || email.isBlank()) {
            throw new IllegalArgumentException("Email requerido");
        }
        if (passPlano == null || passPlano.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
        }

        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setEmail(email);
        u.setPassHash(PasswordHasher.hash(passPlano));
        u.setRolId(rolId);
        u.setActivo(activo);
        
        // Delegar al repositorio
        return repo.insert(u);
    }

    public void editarUsuario(int id, String nombre, String email, int rolId, boolean activo)
            throws IllegalArgumentException {
        
        // --- Lógica de Negocio (Validación) ---
        if (id <= 0) {
            throw new IllegalArgumentException("ID de usuario inválido");
        }
         if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("Nombre requerido");
        }
        if (email == null  || email.isBlank()) {
            throw new IllegalArgumentException("Email requerido");
        }
        
        Usuario u = new Usuario();
        u.setId(id); 
        u.setNombre(nombre); 
        u.setEmail(email); 
        u.setRolId(rolId); 
        u.setActivo(activo);
        
        // Delegar al repositorio
        repo.update(u);
    }

    public void cambiarPassword(int id, String nuevaPass) throws IllegalArgumentException {
        if (id <= 0) {
            throw new IllegalArgumentException("ID de usuario inválido");
        }
        if (nuevaPass == null || nuevaPass.length() < 6) {
            throw new IllegalArgumentException("La nueva contraseña debe tener al menos 6 caracteres");
        }
        
        repo.updatePassword(id, PasswordHasher.hash(nuevaPass));
    }

    public void setActivo(int id, boolean activo) throws IllegalArgumentException {
        if (id <= 0) {
            throw new IllegalArgumentException("ID de usuario inválido");
        }
        repo.setActivo(id, activo); 
    }
}