package com.veterinaria.veterinariaapp.repository;

/**
 *
 * @author Yuriko Matsuo
 * (Refactorizado para implementar la interfaz y ser la única fuente de datos)
 */
import com.veterinaria.veterinariaapp.config.*;
import com.veterinaria.veterinariaapp.model.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// MODIFICACIÓN 1: Añade "implements IClienteRepository"
//          VVVVVVVVVVVVVVVVVVVVVVVVVV
public class ClienteRepository implements IClienteRepository {

    private static final String SQL_INSERT =
        "INSERT INTO cliente (nombre, apellido, direccion, telefono) VALUES (?, ?, ?, ?)";

    @Override // MODIFICACIÓN 2: Añade @Override
    public void registrar(Cliente c) throws Exception {
        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_INSERT)) {

            ps.setString(1, c.getNombre());
            ps.setString(2, c.getApellido());
            ps.setString(3, c.getDireccion());
            ps.setString(4, c.getTelefono());

            ps.executeUpdate();
        }
    }
    
    // Listar cliente
    @Override // MODIFICACIÓN 2: Añade @Override
    public List<Cliente> listar() throws SQLException, Exception {
        String sql = "SELECT id_cliente, nombre, apellido, direccion, telefono " +
                     "FROM cliente ORDER BY id_cliente DESC";
        List<Cliente> list = new ArrayList<>();
        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Cliente c = new Cliente();
                c.setIdCliente(rs.getInt("id_cliente"));
                c.setNombre(rs.getString("nombre"));
                c.setApellido(rs.getString("apellido"));
                c.setDireccion(rs.getString("direccion"));
                c.setTelefono(rs.getString("telefono"));
                list.add(c);
            }
        }
        return list;
    }
    
    // ACTUALIZAR
    @Override // MODIFICACIÓN 2: Añade @Override
    public void actualizar(Cliente c) throws Exception {
        String sql = "UPDATE cliente SET nombre=?, apellido=?, direccion=?, telefono=? " +
                     "WHERE id_cliente=?";
        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getApellido());
            ps.setString(3, c.getDireccion());
            ps.setString(4, c.getTelefono());
            ps.setInt(5, c.getIdCliente());
            ps.executeUpdate();
        }
    }

    // ELIMINAR
    @Override // MODIFICACIÓN 2: Añade @Override
    public void eliminar(int idCliente) throws Exception {
        String sql = "DELETE FROM cliente WHERE id_cliente=?";
        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            ps.executeUpdate();
        }
    }
}