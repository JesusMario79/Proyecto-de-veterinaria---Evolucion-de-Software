package com.veterinaria.veterinariaapp.repository;

import com.veterinaria.veterinariaapp.model.Producto;
import java.sql.SQLException;
import java.util.List;

public interface IProductoRepository {

    List<Producto> findAll() throws SQLException;

    List<Producto> searchByNombre(String q) throws SQLException;

    void insert(Producto p) throws SQLException;

    void update(Producto p) throws SQLException;

    void delete(int id) throws SQLException;
}
