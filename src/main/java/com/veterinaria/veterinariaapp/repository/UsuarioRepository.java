package com.veterinaria.veterinariaapp.repository;

import com.veterinaria.veterinariaapp.config.Db;
import com.veterinaria.veterinariaapp.model.Usuario;

import java.sql.*;
import java.util.*;

public class UsuarioRepository {

    // ======== Lecturas ========

    public List<Usuario> findAll(){
        String sql = """
            SELECT u.id,u.nombre,u.email,u.pass_hash,u.activo,u.rol_id,r.nombre AS rol
            FROM usuarios u JOIN roles r ON r.id=u.rol_id
            ORDER BY u.id DESC
        """;
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Usuario> out = new ArrayList<>();
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNombre(rs.getString("nombre"));
                u.setEmail(rs.getString("email"));
                u.setPassHash(rs.getString("pass_hash"));
                u.setActivo(rs.getBoolean("activo"));
                u.setRolId(rs.getInt("rol_id"));
                u.setRol(rs.getString("rol"));
                out.add(u);
            }
            return out;
        } catch (Exception e) {
            throw new RuntimeException("Error listando usuarios", e);
        }
    }

    public Usuario findById(int id){
        String sql = """
            SELECT u.id,u.nombre,u.email,u.pass_hash,u.activo,u.rol_id,r.nombre AS rol
            FROM usuarios u JOIN roles r ON r.id=u.rol_id WHERE u.id=?
        """;
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNombre(rs.getString("nombre"));
                u.setEmail(rs.getString("email"));
                u.setPassHash(rs.getString("pass_hash"));
                u.setActivo(rs.getBoolean("activo"));
                u.setRolId(rs.getInt("rol_id"));
                u.setRol(rs.getString("rol"));
                return u;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error obteniendo usuario", e);
        }
    }

    public Usuario findByEmail(String email){
        String sql = "SELECT id,nombre,email,pass_hash,activo,rol_id FROM usuarios WHERE email=? LIMIT 1";
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
                u.setRolId(rs.getInt("rol_id"));
                return u;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error consultando usuario", e);
        }
    }

    // ======== Escrituras ========

    public int insert(Usuario u){
        String sql = "INSERT INTO usuarios(nombre,email,pass_hash,activo,rol_id) VALUES(?,?,?,?,?)";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPassHash());
            ps.setBoolean(4, u.isActivo());
            ps.setInt(5, u.getRolId());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            if (e.getSQLState() != null && e.getSQLState().startsWith("23")) {
                throw new IllegalArgumentException("El email ya está registrado.");
            }
            throw new RuntimeException("Error insertando usuario", e);
        }
    }

    public void update(Usuario u){
        String sql = "UPDATE usuarios SET nombre=?, email=?, rol_id=?, activo=? WHERE id=?";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getEmail());
            ps.setInt(3, u.getRolId());
            ps.setBoolean(4, u.isActivo());
            ps.setInt(5, u.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando usuario", e);
        }
    }

    public void updatePassword(int id, String newHash){
        String sql = "UPDATE usuarios SET pass_hash=? WHERE id=?";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, newHash);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error cambiando contraseña", e);
        }
    }

    public void setActivo(int id, boolean activo){
        String sql = "UPDATE usuarios SET activo=? WHERE id=?";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, activo);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error cambiando estado", e);
        }
    }

    // ======== Roles (para combos) ========
    public Map<Integer,String> findAllRoles(){
        String sql = "SELECT id,nombre FROM roles ORDER BY nombre";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            Map<Integer,String> map = new LinkedHashMap<>();
            while (rs.next()) map.put(rs.getInt("id"), rs.getString("nombre"));
            return map;
        } catch (Exception e) {
            throw new RuntimeException("Error listando roles", e);
        }
    }
}
