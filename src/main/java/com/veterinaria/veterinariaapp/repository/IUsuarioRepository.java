package com.veterinaria.veterinariaapp.repository;

import com.veterinaria.veterinariaapp.model.Usuario;
import java.util.List;
import java.util.Map;

/**
 * Esta es la Abstracción (el contrato) para el repositorio de Usuarios.
 * Las clases de servicio (alto nivel) dependerán de ESTA interfaz,
 * no de la clase concreta que usa SQL.
 * Esto cumple con el Principio de Inversión de Dependencia (DIP).
 */
public interface IUsuarioRepository {

    // --- Lecturas ---
    List<Usuario> findAll();
    Usuario findById(int id);
    Usuario findByEmail(String email);
    Map<Integer, String> findAllRoles();

    // --- Escrituras ---
    int insert(Usuario u);
    void update(Usuario u);
    void updatePassword(int id, String newHash);
    void setActivo(int id, boolean activo);
}