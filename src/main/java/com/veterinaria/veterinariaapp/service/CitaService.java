package com.veterinaria.veterinariaapp.service;

import com.veterinaria.veterinariaapp.model.Cita;
import com.veterinaria.veterinariaapp.model.Mascota;
import com.veterinaria.veterinariaapp.repository.ICitaRepository;
import com.veterinaria.veterinariaapp.repository.IMascotaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Capa de servicio para la gestión de Citas.
 * Contiene toda la lógica de negocio (reglas de validación).
 * NO conoce la UI (JFrame).
 * SOLO conoce las abstracciones (Interfaces) del repositorio (DIP).
 */
public class CitaService {

    // Depende de las ABSTRACCIONES (Interfaces), no de clases concretas
    private final ICitaRepository citaRepo;
    private final IMascotaRepository mascotaRepo;

    // Recibe las dependencias (Inyección)
    public CitaService(ICitaRepository citaRepo, IMascotaRepository mascotaRepo) {
        this.citaRepo = citaRepo;
        this.mascotaRepo = mascotaRepo;
    }

    // --- Métodos que la Vista llamará ---

    // (Añadimos 'throws Exception' porque los repositorios los lanzan)

    public List<Cita> listarCitas() throws Exception {
        // Por ahora solo llama al repo, pero podría añadir lógica extra
        return citaRepo.listar();
    }
    
    public List<Mascota> listarMascotas() throws Exception {
        return mascotaRepo.listar();
    }
    
    /**
     * Valida y agenda una nueva cita.
     * @param nuevaCita El objeto Cita con los datos del formulario.
     * @throws Exception Si hay un error de BD.
     * @throws IllegalArgumentException Si falla una regla de negocio.
     */
    public void agendarCita(Cita nuevaCita) throws Exception, IllegalArgumentException {
        //
        // --- ¡AQUÍ VA LA LÓGICA DE NEGOCIO! (SRP) ---
        //
        
        // 1. Validaciones de datos (aunque la vista ya debería haber hecho algunas)
        if (nuevaCita.getMascotaId() == null) throw new IllegalArgumentException("Debe seleccionar una mascota.");
        if (nuevaCita.getFechaHora() == null) throw new IllegalArgumentException("Debe seleccionar una fecha y hora.");
        if (nuevaCita.getMotivo() == null || nuevaCita.getMotivo().trim().isEmpty()) {
            throw new IllegalArgumentException("El motivo no puede estar vacío.");
        }

        // 2. Validaciones de LÓGICA DE NEGOCIO
        if (nuevaCita.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("No se puede agendar una cita en una fecha u hora pasada.");
        }

        // 3. Validación de CONFLICTO (contra la BD)
        if (citaRepo.existeCitaConflictiva(nuevaCita.getFechaHora(), null)) {
            throw new IllegalArgumentException("Ya existe una cita programada para esa fecha y hora.");
        }
        
        // Si todo está bien, inserta
        // (El SP 'sp_crear_cita' ya asigna el estado 'PENDIENTE' por defecto)
        citaRepo.insertar(nuevaCita);
    }
    
    /**
     * Valida y actualiza una cita existente.
     * @param cita El objeto Cita con los datos modificados.
     * @throws Exception Si hay un error de BD.
     * @throws IllegalArgumentException Si falla una regla de negocio.
     */
    public void actualizarCita(Cita cita) throws Exception, IllegalArgumentException {
        //
        // --- LÓGICA DE NEGOCIO PARA ACTUALIZAR ---
        //
        if (cita.getId() == null) throw new IllegalArgumentException("ID de cita inválido para actualizar.");
        
        // Validaciones...
        if (cita.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("No se puede re-agendar una cita en una fecha u hora pasada.");
        }
        
        // Validar conflicto, pero EXCLUYENDO la cita actual
        if (citaRepo.existeCitaConflictiva(cita.getFechaHora(), cita.getId())) {
            throw new IllegalArgumentException("Ya existe otra cita programada para esa fecha y hora.");
        }
        
        // Si todo está bien, actualiza
        citaRepo.actualizar(cita);
    }
    
    /**
     * Cancela (elimina) una cita.
     * @param id El ID de la cita a cancelar.
     * @throws Exception Si hay un error de BD.
     */
    public void cancelarCita(int id) throws Exception {
        // Podría tener lógica aquí (ej. no cancelar si faltan < 1hr), 
        // pero por ahora solo delega la eliminación.
        citaRepo.eliminar(id);
    }
}