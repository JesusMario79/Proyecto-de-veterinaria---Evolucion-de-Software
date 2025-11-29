package com.veterinaria.veterinariaapp.repository;

import com.veterinaria.veterinariaapp.model.Servicios;
import java.util.List;

/**
 * Abstracción (Contrato) para el repositorio de Servicios.
 * Cumple con el Principio de Inversión de Dependencia (DIP).
 */
public interface IServiciosRepository {
    
    int registrar(Servicios s) throws Exception; 
    List<Servicios> listar() throws Exception;
    Servicios buscarPorId(int id) throws Exception;
    void actualizar(Servicios s) throws Exception;
    void eliminar(int idServicio) throws Exception;
    
    // LÍNEA A AÑADIR PARA CORREGIR EL ERROR:
    Servicios findByNombre(String nombre) throws Exception;
}