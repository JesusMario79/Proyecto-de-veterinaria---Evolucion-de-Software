package com.veterinaria.veterinariaapp.service;

import com.veterinaria.veterinariaapp.model.Cliente;
import com.veterinaria.veterinariaapp.repository.IClienteRepository; // Depende de la interfaz

import java.util.List;

/**
 * Capa de servicio para la gestión de Clientes.
 * Contiene toda la lógica de negocio (reglas de validación).
 * Cumple con SRP y DIP.
 */
public class ClienteService {

    private final IClienteRepository clienteRepo;

    public ClienteService(IClienteRepository clienteRepo) {
        this.clienteRepo = clienteRepo;
    }

    public List<Cliente> listarClientes() throws Exception {
        return clienteRepo.listar();
    }

    public void agregarCliente(Cliente cliente) throws Exception, IllegalArgumentException {
        // Lógica de negocio (SRP)
        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty() ||
            cliente.getApellido() == null || cliente.getApellido().trim().isEmpty()) {
            
            throw new IllegalArgumentException("Nombre y Apellido son obligatorios.");
        }
        clienteRepo.registrar(cliente);
    }

    public void actualizarCliente(Cliente cliente) throws Exception, IllegalArgumentException {
        // Lógica de negocio (SRP)
        if (cliente.getIdCliente() == null) {
            throw new IllegalArgumentException("ID de cliente inválido para actualizar.");
        }
        
        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty() ||
            cliente.getApellido() == null || cliente.getApellido().trim().isEmpty()) {
            
            throw new IllegalArgumentException("Nombre y Apellido son obligatorios.");
        }
        clienteRepo.actualizar(cliente);
    }

    public void eliminarCliente(int id) throws Exception {
        clienteRepo.eliminar(id);
    }
}