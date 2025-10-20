package com.veterinaria.veterinariaapp.service;

import com.veterinaria.veterinariaapp.repository.IDashboardRepository;
import java.util.Map;

public class DashboardService {
    private final IDashboardRepository repo;

    public DashboardService(IDashboardRepository repo) {
        this.repo = repo;
    }

    public int totalCitas() throws Exception    { return repo.countCitas(); }
    public int totalMascotas() throws Exception { return repo.countMascotas(); }
    public int totalClientes() throws Exception { return repo.countClientes(); }

    /** hoy ± N días (ej. 6). */
    public Map<String,Integer> citasPorDiaVentana(int diasBack, int diasForward) throws Exception {
        return repo.citasPorDia(diasBack, diasForward);
    }

    /** ✅ Nuevo: citas agrupadas por semana. */
    public Map<String,Integer> citasPorSemanaVentana(int semanasBack, int semanasForward) throws Exception {
        return repo.citasPorSemana(semanasBack, semanasForward);
    }

    public Map<String,Integer> distribucionEspecies(int topN) throws Exception {
        return repo.distribucionEspecies(topN);
    }
}
