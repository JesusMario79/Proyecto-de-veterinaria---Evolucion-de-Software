package com.veterinaria.veterinariaapp.service;

import com.veterinaria.veterinariaapp.model.Pago;
import com.veterinaria.veterinariaapp.repository.IPagoRepository;
import java.math.BigDecimal;
import java.util.List;

public class PagoService {

    // DIP: Dependemos de la interfaz, no de la implementación concreta
    private final IPagoRepository pagoRepo;

    public PagoService(IPagoRepository pagoRepo) {
        this.pagoRepo = pagoRepo;
    }

    public List<Pago> listarPagos() throws Exception {
        return pagoRepo.listar();
    }

    public void registrarPago(Pago pago) throws Exception {
        // Validaciones de negocio (SRP)
        if (pago.getCitaId() == null || pago.getCitaId() <= 0) {
            throw new IllegalArgumentException("El ID de la cita no es válido.");
        }
        
        if (pago.getMonto() == null || pago.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a 0.");
        }

        if (pago.getMetodoPago() == null || pago.getMetodoPago().trim().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar un método de pago.");
        }

        // Si pasa las validaciones, guardamos
        pagoRepo.registrar(pago);
    }
}