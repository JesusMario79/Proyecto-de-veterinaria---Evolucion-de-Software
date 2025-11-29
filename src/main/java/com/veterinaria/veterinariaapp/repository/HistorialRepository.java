package com.veterinaria.veterinariaapp.repository;

import com.veterinaria.veterinariaapp.config.Db;
import com.veterinaria.veterinariaapp.model.HistorialMedico;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HistorialRepository implements IHistorialRepository {

    @Override
    public List<HistorialMedico> listarPorMascota(int mascotaId) throws Exception {
        List<HistorialMedico> lista = new ArrayList<>();
        String sql = "SELECT id, mascota_id, fecha, descripcion, tratamiento " +
                     "FROM historial_medico WHERE mascota_id = ? ORDER BY fecha DESC";

        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setInt(1, mascotaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    HistorialMedico h = new HistorialMedico();
                    h.setId(rs.getInt("id"));
                    h.setMascotaId(rs.getInt("mascota_id"));
                    h.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
                    h.setDescripcion(rs.getString("descripcion"));
                    h.setTratamiento(rs.getString("tratamiento"));
                    lista.add(h);
                }
            }
        }
        return lista;
    }

    @Override
    public void insertar(HistorialMedico h) throws Exception {
        String sql = "INSERT INTO historial_medico (mascota_id, fecha, descripcion, tratamiento) VALUES (?, ?, ?, ?)";
        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setInt(1, h.getMascotaId());
            ps.setTimestamp(2, Timestamp.valueOf(h.getFecha()));
            ps.setString(3, h.getDescripcion());
            ps.setString(4, h.getTratamiento());
            ps.executeUpdate();
        }
    }
}