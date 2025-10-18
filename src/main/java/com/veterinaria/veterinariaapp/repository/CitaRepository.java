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
 * IMPLEMENTA la interfaz para cumplir con DIP.
 */
public class CitaRepository implements ICitaRepository {

    // --- ¡YA NO USAMOS LA VISTA 'v_citas_detalle'! ---
    // private static final String BASE_SELECT = ... (Eliminada)


    /**
     * Recupera todas las citas de la base de datos, ordenadas por fecha descendente.
     * @return una lista de objetos Cita.
     * @throws Exception si ocurre un error de SQL.
     */
    @Override
    public List<Cita> listar() throws Exception {
        List<Cita> citas = new ArrayList<>();
        
        // ¡CONSULTA CORREGIDA!
        // Dejamos de usar la vista y usamos un JOIN para poder obtener el 'mascota_id',
        // que es CRÍTICO para la lógica de "Editar".
        String sql = """
            SELECT ct.id, ct.fecha_hora, ct.motivo, ct.estado, ct.mascota_id,
                   m.nombre AS mascota, CONCAT(cli.nombre,' ',cli.apellido) AS cliente
            FROM citas ct
            JOIN mascota m ON m.id = ct.mascota_id
            LEFT JOIN cliente cli ON cli.id_cliente = m.cliente_id
            ORDER BY ct.fecha_hora DESC
        """;

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
     * @param mascotaId El ID de la mascota cuyo historial se quiere obtener.
     * @return una lista de objetos Cita para esa mascota.
     * @throws Exception si ocurre un error de SQL.
     */
    @Override
    public List<Cita> listarPorMascotaId(int mascotaId) throws Exception {
        List<Cita> citas = new ArrayList<>();
        
        // ¡CONSULTA CORREGIDA! (Añadido ct.mascota_id al SELECT)
        String sql = """
            SELECT ct.id, ct.fecha_hora, ct.motivo, ct.estado, ct.mascota_id,
                   m.nombre AS mascota, CONCAT(cli.nombre,' ',cli.apellido) AS cliente
            FROM citas ct
            JOIN mascota m ON m.id = ct.mascota_id
            LEFT JOIN cliente cli ON cli.id_cliente = m.cliente_id
            WHERE ct.mascota_id = ?
            ORDER BY ct.fecha_hora DESC
        """;

        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setInt(1, mascotaId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
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
    @Override
    public void insertar(Cita cita) throws Exception {
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
     * @param cita El objeto Cita con los datos actualizados.
     * @throws Exception si ocurre un error de SQL.
     */
    @Override
    public void actualizar(Cita cita) throws Exception {
        String sql = "UPDATE citas SET mascota_id = ?, fecha_hora = ?, motivo = ?, estado = ? WHERE id = ?";
        
        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setInt(1, cita.getMascotaId());
            ps.setTimestamp(2, Timestamp.valueOf(cita.getFechaHora()));
            ps.setString(3, cita.getMotivo());
            ps.setString(4, cita.getEstado().toString());
            ps.setInt(5, cita.getId());
            
            ps.executeUpdate();
        }
    }

    /**
     * Elimina una cita de la base de datos por su ID.
     * @param idCita El ID de la cita a eliminar.
     * @throws Exception si ocurre un error de SQL.
     */
    @Override
    public void eliminar(int idCita) throws Exception {
        String sql = "DELETE FROM citas WHERE id = ?";
        
        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setInt(1, idCita);
            ps.executeUpdate();
        }
    }
    
    /**
     * Verifica si existe una cita conflictiva en la misma fecha y minuto.
     * @param fechaHora La fecha y hora a verificar.
     * @param idCitaAExcluir El ID de la cita a ignorar en la búsqueda, o null si es una nueva cita.
     * @return true si se encuentra un conflicto, false en caso contrario.
     * @throws Exception si ocurre un error de SQL.
     */
    @Override
    public boolean existeCitaConflictiva(LocalDateTime fechaHora, Integer idCitaAExcluir) throws Exception {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM citas WHERE DATE_FORMAT(fecha_hora, '%Y-%m-%d %H:%i') = DATE_FORMAT(?, '%Y-%m-%d %H:%i')");

        if (idCitaAExcluir != null) {
            sql.append(" AND id != ?");
        }

        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {
            
            ps.setTimestamp(1, Timestamp.valueOf(fechaHora));
            if (idCitaAExcluir != null) {
                ps.setInt(2, idCitaAExcluir);
            }
            
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
     * @param rs El ResultSet posicionado en la fila a mapear.
     * @return un objeto Cita poblado.
     * @throws SQLException si hay un error al leer el ResultSet.
     */
    private Cita map(ResultSet rs) throws SQLException {
        Cita cita = new Cita();
        cita.setId(rs.getInt("id"));
        cita.setFechaHora(rs.getTimestamp("fecha_hora").toLocalDateTime());
        cita.setMotivo(rs.getString("motivo"));
        cita.setEstado(EstadoCita.fromString(rs.getString("estado")));
        
        // --- ¡ESTA ES LA CORRECCIÓN CRÍTICA! ---
        // Ahora sí estamos leyendo el 'mascota_id' de la BD y
        // guardándolo en el objeto Cita.
        cita.setMascotaId(rs.getInt("mascota_id")); 
        
        // Poblamos los campos auxiliares que vienen del JOIN
        cita.setMascotaNombre(rs.getString("mascota"));
        cita.setClienteNombre(rs.getString("cliente"));
        
        return cita;
    }
}