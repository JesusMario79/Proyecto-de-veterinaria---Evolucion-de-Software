package com.veterinaria.veterinariaapp.model;

import java.time.LocalDate;

/**
 * Representa una Mascota (paciente) de la clínica.
 */
public class Mascota {
    private Integer id;
    private String nombre;
    private String raza;
    private String especie;
    private LocalDate fechaRegistro;
    private LocalDate fechaNacimiento;

    /** FK al cliente (dueño) */
    private Integer clienteId;

    /** Texto auxiliar para mostrar en tablas: nombre + apellido del cliente */
    private String clienteNombre;

    /** Foto en bytes (por ejemplo, almacenada en BLOB) */
    private byte[] foto;

    public Mascota() {}

    public Mascota(Integer id, String nombre, String raza, String especie,
                   LocalDate fechaRegistro, LocalDate fechaNacimiento,
                   Integer clienteId, String clienteNombre, byte[] foto) {
        this.id = id;
        this.nombre = nombre;
        this.raza = raza;
        this.especie = especie;
        this.fechaRegistro = fechaRegistro;
        this.fechaNacimiento = fechaNacimiento;
        this.clienteId = clienteId;
        this.clienteNombre = clienteNombre;
        this.foto = foto;
    }

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getRaza() { return raza; }
    public void setRaza(String raza) { this.raza = raza; }

    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }

    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public byte[] getFoto() { return foto; }
    public void setFoto(byte[] foto) { this.foto = foto; }
}
