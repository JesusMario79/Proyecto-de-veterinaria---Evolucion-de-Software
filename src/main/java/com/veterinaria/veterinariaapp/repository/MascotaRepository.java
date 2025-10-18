package com.veterinaria.veterinariaapp.repository;

import com.veterinaria.veterinariaapp.model.Mascota;
import com.veterinaria.veterinariaapp.config.Db; // ← ajusta si tu Db.java está en otro paquete

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// MODIFICACIÓN 1: Añade "implements IMascotaRepository"
//         VVVVVVVVVVVVVVVVVVVVVVVVVV
public class MascotaRepository implements IMascotaRepository {

    private static final String BASE_SELECT =
        "SELECT m.id, m.nombre, m.raza, m.especie, m.fecha_registro, m.fecha_nacimiento, " +
        "       m.cliente_id, CONCAT(c.nombre,' ',c.apellido) AS cliente, m.foto " +
        "FROM mascota m LEFT JOIN cliente c ON c.id_cliente = m.cliente_id ";

    @Override // MODIFICACIÓN 2: Añade @Override
    public List<Mascota> listar() throws Exception {
        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement(BASE_SELECT + " ORDER BY m.id DESC");
             ResultSet rs = ps.executeQuery()) {
            List<Mascota> out = new ArrayList<>();
            while (rs.next()) out.add(map(rs));
            return out;
        }
    }

    @Override // MODIFICACIÓN 2: Añade @Override
    public void insertar(Mascota m) throws Exception {
        String sql = "INSERT INTO mascota(nombre, raza, especie, fecha_registro, fecha_nacimiento, cliente_id, foto) " +
                     "VALUES (?,?,?,?,?,?,?)";
        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, m.getNombre());
            ps.setString(2, m.getRaza());
            ps.setString(3, m.getEspecie());
            ps.setObject(4, m.getFechaRegistro());     // JDBC 4.2 soporta LocalDate
            ps.setObject(5, m.getFechaNacimiento());
            if (m.getClienteId() == null) ps.setNull(6, Types.INTEGER); else ps.setInt(6, m.getClienteId());
            if (m.getFoto() == null) ps.setNull(7, Types.BLOB); else ps.setBytes(7, m.getFoto());
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) { if (gk.next()) m.setId(gk.getInt(1)); }
        }
    }

    @Override // MODIFICACIÓN 2: Añade @Override
    public void actualizar(Mascota m) throws Exception {
        String sql = "UPDATE mascota SET nombre=?, raza=?, especie=?, fecha_registro=?, fecha_nacimiento=?, " +
                     "cliente_id=?, foto=? WHERE id=?";
        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, m.getNombre());
            ps.setString(2, m.getRaza());
            ps.setString(3, m.getEspecie());
            ps.setObject(4, m.getFechaRegistro());
            ps.setObject(5, m.getFechaNacimiento());
            if (m.getClienteId() == null) ps.setNull(6, Types.INTEGER); else ps.setInt(6, m.getClienteId());
            if (m.getFoto() == null) ps.setNull(7, Types.BLOB); else ps.setBytes(7, m.getFoto());
            ps.setInt(8, m.getId());
            ps.executeUpdate();
        }
    }

    @Override // MODIFICACIÓN 2: Añade @Override
    public void eliminar(int id) throws Exception {
        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement("DELETE FROM mascota WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // ---- utilidades ----
    // (El método 'map' es privado, así que NO lleva @Override)
    private Mascota map(ResultSet rs) throws SQLException {
        Mascota m = new Mascota();
        m.setId(rs.getInt("id"));
        m.setNombre(rs.getString("nombre"));
        m.setRaza(rs.getString("raza"));
        m.setEspecie(rs.getString("especie"));
        m.setFechaRegistro(rs.getObject("fecha_registro", LocalDate.class));
        m.setFechaNacimiento(rs.getObject("fecha_nacimiento", LocalDate.class));
        m.setClienteId((Integer) rs.getObject("cliente_id"));
        m.setClienteNombre(rs.getString("cliente"));
        m.setFoto(rs.getBytes("foto"));
        return m;
    }
}