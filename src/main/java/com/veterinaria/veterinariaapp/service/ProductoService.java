package com.veterinaria.veterinariaapp.service;

import com.veterinaria.veterinariaapp.model.Producto;
import com.veterinaria.veterinariaapp.repository.IProductoRepository;

import java.util.List;

/**
 * Capa de servicio para la gestión de Productos.
 * Contiene toda la lógica de negocio (reglas de validación).
 * Cumple con SRP y DIP.
 */
public class ProductoService {

    private final IProductoRepository productoRepo;

    /**
     * Recibe la implementación de IProductoRepository por inyección
     * (respeta el Principio de Inversión de Dependencias).
     */
    public ProductoService(IProductoRepository productoRepo) {
        this.productoRepo = productoRepo;
    }

    public List<Producto> listarProductos() throws Exception {
        return productoRepo.findAll();
    }

    public List<Producto> buscarPorNombre(String q) throws Exception {
        if (q == null || q.trim().isEmpty()) {
            return listarProductos();
        }
        return productoRepo.searchByNombre(q.trim());
    }

    public void agregarProducto(Producto producto) throws Exception, IllegalArgumentException {
        validarProducto(producto);
        // Para un producto nuevo asumimos id = 0 ó null
        productoRepo.insert(producto);
    }

    public void actualizarProducto(Producto producto) throws Exception, IllegalArgumentException {
        if (producto.getId() <= 0) {
            throw new IllegalArgumentException("ID de producto inválido para actualizar.");
        }
        validarProducto(producto);
        productoRepo.update(producto);
    }

    public void eliminarProducto(int id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("ID de producto inválido para eliminar.");
        }
        productoRepo.delete(id);
    }

    // ====== Reglas de negocio / validación ======
    private void validarProducto(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }

        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }

        // La categoría puedes volverla obligatoria o no, según tu profe.
        if (producto.getCategoria() == null || producto.getCategoria().trim().isEmpty()) {
            throw new IllegalArgumentException("La categoría del producto es obligatoria.");
        }

        if (producto.getPrecio() < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }

        if (producto.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }
    }
}
