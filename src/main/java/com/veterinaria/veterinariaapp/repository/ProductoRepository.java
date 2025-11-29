package com.veterinaria.veterinariaapp.repository;

import com.veterinaria.veterinariaapp.config.Db;
import com.veterinaria.veterinariaapp.model.Producto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoRepository implements IProductoRepository {

    private Connection getConnection() throws SQLException {
        return Db.getConnection(); // igual que en tus otros repositorios
    }

    @Override
    public List<Producto> findAll() throws SQLException {
        String sql = "SELECT id, nombre, precio, stock, categoria, created_at FROM productos";
        List<Producto> lista = new ArrayList<>();

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    @Override
    public List<Producto> searchByNombre(String q) throws SQLException {
        String sql = "SELECT id, nombre, precio, stock, categoria, created_at " +
                     "FROM productos WHERE nombre LIKE ?";
        List<Producto> lista = new ArrayList<>();

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + q + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRow(rs));
                }
            }
        }
        return lista;
    }

    @Override
    public void insert(Producto p) throws SQLException {
        String sql = "INSERT INTO productos (nombre, precio, stock, categoria) VALUES (?,?,?,?)";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getNombre());
            ps.setDouble(2, p.getPrecio());
            ps.setInt(3, p.getStock());
            ps.setString(4, p.getCategoria());
            ps.executeUpdate();
        }
    }

    @Override
    public void update(Producto p) throws SQLException {
        String sql = "UPDATE productos SET nombre=?, precio=?, stock=?, categoria=? WHERE id=?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getNombre());
            ps.setDouble(2, p.getPrecio());
            ps.setInt(3, p.getStock());
            ps.setString(4, p.getCategoria());
            ps.setInt(5, p.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM productos WHERE id=?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Producto mapRow(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.setId(rs.getInt("id"));
        p.setNombre(rs.getString("nombre"));
        p.setPrecio(rs.getDouble("precio"));
        p.setStock(rs.getInt("stock"));
        p.setCategoria(rs.getString("categoria"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        return p;
    }
}
