package com.veterinaria.veterinariaapp.service;

import com.veterinaria.veterinariaapp.model.Cliente;
import com.veterinaria.veterinariaapp.model.Mascota;
import com.veterinaria.veterinariaapp.repository.IClienteRepository;
import com.veterinaria.veterinariaapp.repository.IMascotaRepository;

import java.util.List;

/**
 * Capa de servicio para la gestión de Mascotas.
 * Contiene la lógica de negocio (validaciones) y coordina
 * los repositorios de Mascotas y Clientes.
 * Cumple con SRP y DIP.
 */
public class MascotaService {

    private final IMascotaRepository mascotaRepo;
    private final IClienteRepository clienteRepo;

    // Recibe AMBAS interfaces por inyección de dependencia
    public MascotaService(IMascotaRepository mascotaRepo, IClienteRepository clienteRepo) {
        this.mascotaRepo = mascotaRepo;
        this.clienteRepo = clienteRepo;
    }

    // --- Métodos para el CRUD de Mascotas ---

    public List<Mascota> listarMascotas() throws Exception {
        return mascotaRepo.listar();
    }

    public void agregarMascota(Mascota mascota) throws Exception, IllegalArgumentException {
        // Lógica de negocio (SRP)
        if (mascota.getNombre() == null || mascota.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la mascota es obligatorio.");
        }
        // (Aquí podrían ir más validaciones, ej. no permitir fechas de registro futuras)
        
        mascotaRepo.insertar(mascota);
    }

    public void actualizarMascota(Mascota mascota) throws Exception, IllegalArgumentException {
        // Lógica de negocio (SRP)
        if (mascota.getId() == null) {
            throw new IllegalArgumentException("ID de mascota inválido para actualizar.");
        }
        if (mascota.getNombre() == null || mascota.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la mascota es obligatorio.");
        }
        
        mascotaRepo.actualizar(mascota);
    }

    public void eliminarMascota(int idMascota) throws Exception {
        // (Aquí podría ir lógica como: "no eliminar si tiene citas pendientes")
        // Por ahora, solo delega.
        mascotaRepo.eliminar(idMascota);
    }

    // --- Métodos de coordinación ---

    /**
     * Obtiene la lista de clientes.
     * La Vista de Mascotas llamará a este método en lugar de
     * hacer un SELECT por su cuenta.
     */
    public List<Cliente> listarClientes() throws Exception {
        // Simplemente delega al repositorio de clientes
        return clienteRepo.listar();
    }
}