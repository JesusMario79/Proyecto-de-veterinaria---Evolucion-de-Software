package com.veterinaria.veterinariaapp.repository;

import com.veterinaria.veterinariaapp.model.Pago;
import java.util.List;

public interface IPagoRepository {
    void registrar(Pago pago) throws Exception;
    List<Pago> listar() throws Exception;
}