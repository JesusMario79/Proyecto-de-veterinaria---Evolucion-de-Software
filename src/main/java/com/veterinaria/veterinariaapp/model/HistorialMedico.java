package com.veterinaria.veterinariaapp.model;

import java.time.LocalDateTime;

public class HistorialMedico {
    private Integer id;
    private Integer mascotaId;
    private LocalDateTime fecha;
    private String descripcion;
    private String tratamiento;

    public HistorialMedico() {
    }

    public HistorialMedico(Integer mascotaId, LocalDateTime fecha, String descripcion, String tratamiento) {
        this.mascotaId = mascotaId;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.tratamiento = tratamiento;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getMascotaId() { return mascotaId; }
    public void setMascotaId(Integer mascotaId) { this.mascotaId = mascotaId; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getTratamiento() { return tratamiento; }
    public void setTratamiento(String tratamiento) { this.tratamiento = tratamiento; }
}