package com.veterinaria.veterinariaapp.repository;

import com.veterinaria.veterinariaapp.model.Mascota;
import java.util.List;

/**
 * Abstracción (Contrato) para el repositorio de Mascotas.
 * Cumple con el Principio de Inversión de Dependencia (DIP).
 */
public interface IMascotaRepository {

    // Todos los métodos públicos de tu clase, con 'throws Exception'
    
    List<Mascota> listar() throws Exception;
    
    void insertar(Mascota m) throws Exception;
    
    void actualizar(Mascota m) throws Exception;
    
    void eliminar(int id) throws Exception;
    
}