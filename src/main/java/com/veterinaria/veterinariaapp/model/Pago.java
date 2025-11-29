package com.veterinaria.veterinariaapp.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Pago {
    private Integer id;
    private Integer citaId;
    private BigDecimal monto;
    private LocalDateTime fecha;
    private String metodoPago;

    public Pago() {}

    public Pago(Integer citaId, BigDecimal monto, String metodoPago) {
        this.citaId = citaId;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.fecha = LocalDateTime.now();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getCitaId() { return citaId; }
    public void setCitaId(Integer citaId) { this.citaId = citaId; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
}