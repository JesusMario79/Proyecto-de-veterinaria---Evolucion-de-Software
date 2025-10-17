package com.veterinaria.veterinariaapp.repository;

import com.veterinaria.veterinariaapp.config.Db;
import com.veterinaria.veterinariaapp.model.Cita;
import com.veterinaria.veterinariaapp.model.EstadoCita;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona las operaciones CRUD para la entidad Cita en la base de datos.
 */
public class CitaRepository {

    /**
     * Consulta base que utiliza la vista 'v_citas_detalle' para obtener
     * información enriquecida de las citas.
     */
    private static final String BASE_SELECT =
        "SELECT id, fecha_hora, motivo, estado, mascota, cliente " +
        "FROM v_citas_detalle";
        // Nota: eliminé tel_cliente del SELECT ya que no se mapea en el modelo Cita.

    /**
     * Recupera todas las citas de la base de datos, ordenadas por fecha descendente.
     * @return una lista de objetos Cita.
     * @throws Exception si ocurre un error de SQL.
     */
    public List<Cita> listar() throws Exception {
        List<Cita> citas = new ArrayList<>();
        // Ordenamos por fecha para mostrar las más recientes primero
        String sql = BASE_SELECT + " ORDER BY fecha_hora DESC";

        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                citas.add(map(rs));
            }
        }
        return citas;
    }
    
    /**
     * Recupera el historial de citas para una mascota específica.
     * Los resultados se ordenan por fecha descendente (la más reciente primero).
     * @param mascotaId El ID de la mascota cuyo historial se quiere obtener.
     * @return una lista de objetos Cita para esa mascota.
     * @throws Exception si ocurre un error de SQL.
     */
    public List<Cita> listarPorMascotaId(int mascotaId) throws Exception {
        List<Cita> citas = new ArrayList<>();
        // Usamos la misma vista 'v_citas_detalle' pero añadimos un filtro WHERE.
        // OJO: La vista no tiene 'mascota_id', así que consultamos la tabla 'citas' directamente
        // y hacemos el JOIN para obtener los datos enriquecidos.
        String sql = "SELECT ct.id, ct.fecha_hora, ct.motivo, ct.estado, " +
                     "       m.nombre AS mascota, CONCAT(cli.nombre,' ',cli.apellido) AS cliente " +
                     "FROM citas ct " +
                     "JOIN mascota m ON m.id = ct.mascota_id " +
                     "LEFT JOIN cliente cli ON cli.id_cliente = m.cliente_id " +
                     "WHERE ct.mascota_id = ? " +
                     "ORDER BY ct.fecha_hora DESC";

        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setInt(1, mascotaId); // Asignamos el ID de la mascota al parámetro de la consulta
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Reutilizamos nuestro método 'map' existente. ¡Eficiencia!
                    citas.add(map(rs));
                }
            }
        }
        return citas;
    }

    /**
     * Inserta una nueva cita en la base de datos llamando al procedimiento almacenado.
     * @param cita El objeto Cita a insertar (necesita mascotaId, fechaHora y motivo).
     * @throws Exception si ocurre un error de SQL.
     */
    public void insertar(Cita cita) throws Exception {
        // Usamos el Stored Procedure que ya está en la BD
        String sql = "{CALL sp_crear_cita(?, ?, ?)}";
        
        try (Connection cn = Db.getConnection();
             CallableStatement cstmt = cn.prepareCall(sql)) {
            
            cstmt.setInt(1, cita.getMascotaId());
            cstmt.setTimestamp(2, Timestamp.valueOf(cita.getFechaHora()));
            cstmt.setString(3, cita.getMotivo());
            
            cstmt.executeUpdate();
        }
    }

    /**
     * Actualiza una cita existente en la base de datos.
     * Principalmente para cambiar fecha, motivo o estado.
     * @param cita El objeto Cita con los datos actualizados.
     * @throws Exception si ocurre un error de SQL.
     */
    public void actualizar(Cita cita) throws Exception {
        String sql = "UPDATE citas SET mascota_id = ?, fecha_hora = ?, motivo = ?, estado = ? WHERE id = ?";
        
        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setInt(1, cita.getMascotaId());
            ps.setTimestamp(2, Timestamp.valueOf(cita.getFechaHora()));
            ps.setString(3, cita.getMotivo());
            ps.setString(4, cita.getEstado().toString()); // Usamos el toString() de nuestro Enum
            ps.setInt(5, cita.getId());
            
            ps.executeUpdate();
        }
    }

    /**
     * Elimina una cita de la base de datos por su ID.
     * @param idCita El ID de la cita a eliminar.
     * @throws Exception si ocurre un error de SQL.
     */
    public void eliminar(int idCita) throws Exception {
        String sql = "DELETE FROM citas WHERE id = ?";
        
        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setInt(1, idCita);
            ps.executeUpdate();
        }
    }
    
    /**
     * Verifica si ya existe una cita programada que comience en el mismo minuto.
     * Ignora los segundos y milisegundos para evitar falsos negativos.
     * @param fechaHora La fecha y hora a verificar.
     * @return true si ya existe una cita, false en caso contrario.
     * @throws Exception si ocurre un error de SQL.
     */
    public boolean existeCitaEnHora(LocalDateTime fechaHora) throws Exception {
        // SQL para buscar citas que caen en el mismo minuto.
        // TRUNCATE elimina los segundos/milisegundos para una comparación precisa.
        String sql = "SELECT COUNT(*) FROM citas WHERE DATE_FORMAT(fecha_hora, '%Y-%m-%d %H:%i') = DATE_FORMAT(?, '%Y-%m-%d %H:%i')";
        
        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setTimestamp(1, Timestamp.valueOf(fechaHora));
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Mapea una fila de un ResultSet a un objeto Cita.
     * Este método centraliza la lógica de conversión.
     * @param rs El ResultSet posicionado en la fila a mapear.
     * @return un objeto Cita poblado.
     * @throws SQLException si hay un error al leer el ResultSet.
     */
    private Cita map(ResultSet rs) throws SQLException {
        Cita cita = new Cita();
        cita.setId(rs.getInt("id"));
        cita.setFechaHora(rs.getTimestamp("fecha_hora").toLocalDateTime());
        cita.setMotivo(rs.getString("motivo"));
        
        // Usamos nuestro método de utilidad en el Enum para convertir String a Enum
        cita.setEstado(EstadoCita.fromString(rs.getString("estado")));
        
        // Poblamos los campos auxiliares que vienen de la vista
        cita.setMascotaNombre(rs.getString("mascota"));
        cita.setClienteNombre(rs.getString("cliente"));
        
        // Nota: La vista no nos da el mascota_id. Si se necesitara para la lógica de edición,
        // se debería añadir a la vista en la BD o hacer un SELECT a la tabla 'citas'.
        // Para la visualización y operaciones básicas con el ID de la cita, esto es suficiente.
        
        return cita;
    }
}