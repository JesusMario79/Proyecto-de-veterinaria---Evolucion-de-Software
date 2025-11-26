package com.veterinaria.veterinariaapp.repository;

import com.veterinaria.veterinariaapp.model.Cita;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Abstracción (Contrato) para el repositorio de Citas.
 * Cumple con el Principio de Inversión de Dependencia (DIP).
 * Las clases de servicio (alto nivel) dependerán de ESTA interfaz.
 */
public interface ICitaRepository {

    // Nota: Añadimos 'throws Exception' porque la implementación
    // concreta (con JDBC) los arroja.
    
    List<Cita> listar() throws Exception;
    
    List<Cita> listarPorMascotaId(int mascotaId) throws Exception;
    
    void insertar(Cita cita) throws Exception;
    
    void actualizar(Cita cita) throws Exception;
    
    void eliminar(int idCita) throws Exception;
    
    boolean existeCitaConflictiva(LocalDateTime fechaHora, Integer idCitaAExcluir) throws Exception;
}