package com.veterinaria.veterinariaapp.repository;

import com.veterinaria.veterinariaapp.model.Cliente;
import java.util.List;

/**
 * Abstracción (Contrato) para el repositorio de Clientes.
 * Cumple con el Principio de Inversión de Dependencia (DIP).
 */
public interface IClienteRepository {

    void registrar(Cliente c) throws Exception;

    List<Cliente> listar() throws Exception;

    void actualizar(Cliente c) throws Exception;

    void eliminar(int idCliente) throws Exception;
    
}