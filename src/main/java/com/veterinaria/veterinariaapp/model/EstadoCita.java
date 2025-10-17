package com.veterinaria.veterinariaapp.model;

public enum EstadoCita {
    // Cambiamos el nombre del miembro del enum y el texto asociado
    PENDIENTE("Pendiente"), // ANTES: PROGRAMADA("Programada")
    REALIZADA("Realizada"),
    CANCELADA("Cancelada");

    private final String nombreVisible;

    EstadoCita(String nombreVisible) {
        this.nombreVisible = nombreVisible;
    }

    @Override
    public String toString() {
        return this.nombreVisible;
    }

    public static EstadoCita fromString(String texto) {
        for (EstadoCita estado : EstadoCita.values()) {
            if (estado.nombreVisible.equalsIgnoreCase(texto)) {
                return estado;
            }
        }
        // Cambiamos el valor por defecto
        return PENDIENTE; // ANTES: return PROGRAMADA;
    }
}