package com.veterinaria.veterinariaapp.service;

import com.veterinaria.veterinariaapp.model.Servicios;
import com.veterinaria.veterinariaapp.repository.IServiciosRepository; // Depende de la interfaz

import java.math.BigDecimal;
import java.util.List;

/**
 * Capa de servicio para la gestión de Servicios.
 * Contiene toda la lógica de negocio (reglas de validación).
 * Cumple con SRP y DIP.
 */
public class ServiciosService {

    private final IServiciosRepository serviciosRepo;

    // Inyección de Dependencia a través del constructor (DIP)
    public ServiciosService(IServiciosRepository serviciosRepo) {
        this.serviciosRepo = serviciosRepo;
    }

    // --- Lecturas ---

    public List<Servicios> listarServicios() throws Exception {
        return serviciosRepo.listar();
    }
    
    public Servicios obtenerServicioPorId(int id) throws Exception {
        return serviciosRepo.buscarPorId(id);
    }

    // --- Escrituras ---

    public int agregarServicio(Servicios servicio) throws Exception, IllegalArgumentException {
        // Lógica de negocio (SRP): Validaciones antes de registrar
        validarDatosServicio(servicio);
        
        // La lógica de negocio podría incluir verificar si ya existe un servicio con ese nombre
        if (serviciosRepo.findByNombre(servicio.getNombre()) != null) {
            throw new IllegalArgumentException("Ya existe un servicio registrado con el nombre: " + servicio.getNombre());
        }

        return serviciosRepo.registrar(servicio);
    }

    public void actualizarServicio(Servicios servicio) throws Exception, IllegalArgumentException {
        // Lógica de negocio (SRP): Validaciones antes de actualizar
        if (servicio.getId() == 0) {
            throw new IllegalArgumentException("ID de servicio inválido para actualizar.");
        }
        validarDatosServicio(servicio);

        serviciosRepo.actualizar(servicio);
    }

    public void eliminarServicio(int id) throws Exception {
        // Podrías añadir lógica de negocio aquí, como verificar si el servicio está en uso
        // antes de eliminarlo.
        serviciosRepo.eliminar(id);
    }
    
    // --- Utilidad (Reglas de Negocio) ---

    private void validarDatosServicio(Servicios servicio) throws IllegalArgumentException {
        if (servicio.getNombre() == null || servicio.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El Nombre del servicio es obligatorio.");
        }
        
        // El precio debe ser obligatorio y mayor que cero.
        if (servicio.getPrecio() == null || servicio.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El Precio debe ser mayor a cero.");
        }
    }
}