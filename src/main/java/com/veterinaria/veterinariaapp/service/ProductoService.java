package com.veterinaria.veterinariaapp.service;

import com.veterinaria.veterinariaapp.model.Producto;
import com.veterinaria.veterinariaapp.repository.IProductoRepository;
import com.veterinaria.veterinariaapp.repository.ProductoRepository;
import java.sql.SQLException;
import java.util.List;

public class ProductoService {

    private final IProductoRepository repo;

    public ProductoService() {
        this.repo = new ProductoRepository();
    }

    public List<Producto> listar() throws SQLException {
        return repo.findAll();
    }

    public List<Producto> buscar(String q) throws SQLException {
        if (q == null || q.trim().isEmpty()) {
            return listar();
        }
        return repo.searchByNombre(q.trim());
    }

    public void guardar(Producto p) throws SQLException {
        if (p.getId() == 0) {
            repo.insert(p);
        } else {
            repo.update(p);
        }
    }

    public void eliminar(int id) throws SQLException {
        repo.delete(id);
    }
}
