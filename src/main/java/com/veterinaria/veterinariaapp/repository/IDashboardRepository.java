package com.veterinaria.veterinariaapp.repository;

import java.util.Map;

public interface IDashboardRepository {
    int countCitas() throws Exception;
    int countMascotas() throws Exception;
    int countClientes() throws Exception;

    /** Ventana de fechas: hoy - diasBack a hoy + diasForward (inclusive). Claves d/M/yyyy en orden cronológico. */
    Map<String,Integer> citasPorDia(int diasBack, int diasForward) throws Exception;

    /** Distribución por especie (top N; el resto en "Otros"). */
    Map<String,Integer> distribucionEspecies(int topN) throws Exception;

    /** ✅ NUEVO: Agrupa las citas por semana (año+semana ISO). */
    Map<String,Integer> citasPorSemana(int semanasBack, int semanasForward) throws Exception;
}
