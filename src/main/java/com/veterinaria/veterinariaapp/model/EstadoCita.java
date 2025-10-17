package com.veterinaria.veterinariaapp.model;

/**
 * Representa los posibles estados de una Cita.
 * Corresponde al tipo ENUM('Programada','Realizada','Cancelada') de la base de datos.
 */
public enum EstadoCita {
    PROGRAMADA("Programada"),
    REALIZADA("Realizada"),
    CANCELADA("Cancelada");

    private final String nombreVisible;

    EstadoCita(String nombreVisible) {
        this.nombreVisible = nombreVisible;
    }

    /**
     * Devuelve el nombre amigable para mostrar en la interfaz.
     * @return El nombre del estado tal como está en la base de datos.
     */
    @Override
    public String toString() {
        return this.nombreVisible;
    }

    /**
     * Método de utilidad para convertir un String de la BD a un Enum.
     * @param texto El texto del estado (ej. "Programada").
     * @return El valor del Enum correspondiente, o PROGRAMADA por defecto si no se encuentra.
     */
    public static EstadoCita fromString(String texto) {
        for (EstadoCita estado : EstadoCita.values()) {
            if (estado.nombreVisible.equalsIgnoreCase(texto)) {
                return estado;
            }
        }
        // Valor por defecto en caso de un estado inesperado o nulo
        return PROGRAMADA;
    }
}