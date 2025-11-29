package com.veterinaria.veterinariaapp.model;

import java.math.BigDecimal;

/**
 *
 * @author Yuriko Matsuo
 */
public class Servicios {
    private int id;
    private String nombre;
    private BigDecimal precio; 
    private String descripcion;
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public BigDecimal getPrecio() {
        return precio;
    }
    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
