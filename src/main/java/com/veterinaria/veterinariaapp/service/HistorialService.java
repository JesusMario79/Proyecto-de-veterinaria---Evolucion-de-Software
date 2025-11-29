package com.veterinaria.veterinariaapp.service;

import com.veterinaria.veterinariaapp.model.HistorialMedico;
import com.veterinaria.veterinariaapp.repository.IHistorialRepository;
import java.util.List;

public class HistorialService {
    private final IHistorialRepository repo;

    public HistorialService(IHistorialRepository repo) {
        this.repo = repo;
    }

    public List<HistorialMedico> listarPorMascota(int mascotaId) throws Exception {
        return repo.listarPorMascota(mascotaId);
    }

    public void registrarEntrada(HistorialMedico h) throws Exception {
        if (h.getMascotaId() == null) {
             throw new IllegalArgumentException("Debe seleccionar una mascota.");
        }
        if (h.getDescripcion() == null || h.getDescripcion().trim().isEmpty()) {
            throw new IllegalArgumentException("El diagnóstico es obligatorio.");
        }
        repo.insertar(h);
    }
}