package com.veterinaria.veterinariaapp.repository;

import com.veterinaria.veterinariaapp.model.HistorialMedico;
import java.util.List;

public interface IHistorialRepository {
    List<HistorialMedico> listarPorMascota(int mascotaId) throws Exception;
    void insertar(HistorialMedico historial) throws Exception;
}