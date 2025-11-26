package com.veterinaria.veterinariaapp.model;

import java.time.LocalDateTime;

/**
 * Representa una Cita en la clínica.
 * Corresponde a la tabla 'citas' y se enriquece con datos de la vista 'v_citas_detalle'.
 */
public class Cita {

    private Integer id;

    /** FK a la mascota. En la BD es mascota_id. */
    private Integer mascotaId;

    private LocalDateTime fechaHora;
    private String motivo;
    private EstadoCita estado; // Usamos nuestro Enum

    // --- Campos auxiliares para visualización (vienen de la vista v_citas_detalle) ---
    private String mascotaNombre;
    private String clienteNombre;

    public Cita() {
    }

    // --- Getters y Setters ---
    // (En NetBeans: clic derecho > Insert Code... > Getter and Setter...)

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMascotaId() {
        return mascotaId;
    }

    public void setMascotaId(Integer mascotaId) {
        this.mascotaId = mascotaId;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public EstadoCita getEstado() {
        return estado;
    }

    public void setEstado(EstadoCita estado) {
        this.estado = estado;
    }

    public String getMascotaNombre() {
        return mascotaNombre;
    }

    public void setMascotaNombre(String mascotaNombre) {
        this.mascotaNombre = mascotaNombre;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    /**
     * Sobrescribimos el método toString para que sea fácil de mostrar en un ComboBox o lista.
     * Esto es opcional pero muy útil para debugging y UI.
     */
    @Override
    public String toString() {
        return "Cita #" + id + " - " + mascotaNombre + " (" + fechaHora + ")";
    }
}