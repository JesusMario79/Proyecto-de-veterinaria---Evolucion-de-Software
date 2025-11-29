package com.veterinaria.veterinariaapp.repository;

import com.veterinaria.veterinariaapp.config.Db;
import com.veterinaria.veterinariaapp.model.Servicios;

import java.sql.*;
import java.util.*;

/**
 *
 * @author Yuriko Matsuo
 */
public class ServicioRepository implements IServiciosRepository {

    // ======== Lecturas ========

    @Override
    public List<Servicios> listar(){ // CAMBIO: De findAll a listar
        String sql = "SELECT id, nombre, precio, descripcion FROM servicios ORDER BY id DESC";
        List<Servicios> out = new ArrayList<>();
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.add(mapRowToServicios(rs));
            }
            return out;
        } catch (Exception e) {
            throw new RuntimeException("Error listando servicios", e);
        }
    }

    @Override
    public Servicios buscarPorId(int id){ // CAMBIO: De findById a buscarPorId
        String sql = "SELECT id, nombre, precio, descripcion FROM servicios WHERE id=?";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapRowToServicios(rs);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error obteniendo servicio por ID", e);
        }
    }

    // Nota: findByNombre no estaba en el IClienteRepository, lo mantengo con el mismo nombre
    public Servicios findByNombre(String nombre){
        String sql = "SELECT id, nombre, precio, descripcion FROM servicios WHERE nombre=? LIMIT 1";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapRowToServicios(rs);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error consultando servicio por nombre", e);
        }
    }

    // ======== Escrituras ========

    @Override
    public int registrar(Servicios s){ // CAMBIO: De insert a registrar
        String sql = "INSERT INTO servicios(nombre, precio, descripcion) VALUES(?,?,?)";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, s.getNombre());
            ps.setBigDecimal(2, s.getPrecio());
            ps.setString(3, s.getDescripcion());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error insertando servicio", e);
        }
    }

    @Override
    public void actualizar(Servicios s){ // CAMBIO: De update a actualizar
        String sql = "UPDATE servicios SET nombre=?, precio=?, descripcion=? WHERE id=?";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, s.getNombre());
            ps.setBigDecimal(2, s.getPrecio());
            ps.setString(3, s.getDescripcion());
            ps.setInt(4, s.getId());

            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando servicio", e);
        }
    }

    @Override
    public void eliminar(int id){ // CAMBIO: De delete a eliminar
        String sql = "DELETE FROM servicios WHERE id=?";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error eliminando servicio", e);
        }
    }

    // ======== Utilidad: Mapeador de Filas ========

    /**
     * Convierte una fila de ResultSet a un objeto Servicios.
     */
    private Servicios mapRowToServicios(ResultSet rs) throws SQLException {
        Servicios s = new Servicios();
        s.setId(rs.getInt("id"));
        s.setNombre(rs.getString("nombre"));
        s.setPrecio(rs.getBigDecimal("precio"));
        s.setDescripcion(rs.getString("descripcion"));
        return s;
    }
}
