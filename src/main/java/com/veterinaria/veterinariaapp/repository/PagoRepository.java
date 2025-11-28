package com.veterinaria.veterinariaapp.repository;

import com.veterinaria.veterinariaapp.config.Db;
import com.veterinaria.veterinariaapp.model.Pago;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PagoRepository implements IPagoRepository {

    @Override
    public void registrar(Pago pago) throws Exception {
        String sql = "INSERT INTO pagos (cita_id, monto, metodo_pago, fecha) VALUES (?, ?, ?, ?)";
        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setInt(1, pago.getCitaId());
            ps.setBigDecimal(2, pago.getMonto());
            ps.setString(3, pago.getMetodoPago());
            // Convertimos LocalDateTime a Timestamp para MySQL
            ps.setTimestamp(4, Timestamp.valueOf(pago.getFecha()));
            
            ps.executeUpdate();
        }
    }

    @Override
    public List<Pago> listar() throws Exception {
        List<Pago> lista = new ArrayList<>();
        String sql = "SELECT id, cita_id, monto, metodo_pago, fecha FROM pagos ORDER BY id DESC";
        
        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Pago p = new Pago();
                p.setId(rs.getInt("id"));
                p.setCitaId(rs.getInt("cita_id"));
                p.setMonto(rs.getBigDecimal("monto"));
                p.setMetodoPago(rs.getString("metodo_pago"));
                p.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
                lista.add(p);
            }
        }
        return lista;
    }
}