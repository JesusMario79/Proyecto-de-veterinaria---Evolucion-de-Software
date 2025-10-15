package com.veterinaria.veterinariaapp.repository;

import com.veterinaria.veterinariaapp.config.Db;
import com.veterinaria.veterinariaapp.model.Usuario;

import java.sql.*;

public class UsuarioRepository {
    public Usuario findByEmail(String email){
        String sql = """
            SELECT u.id,u.nombre,u.email,u.pass_hash,u.activo,r.nombre AS rol
            FROM usuarios u LEFT JOIN roles r ON r.id=u.rol_id
            WHERE u.email=? LIMIT 1
        """;
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNombre(rs.getString("nombre"));
                u.setEmail(rs.getString("email"));
                u.setPassHash(rs.getString("pass_hash"));
                u.setActivo(rs.getBoolean("activo"));
                u.setRol(rs.getString("rol"));
                return u;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error consultando usuario", e);
        }
    }
}
