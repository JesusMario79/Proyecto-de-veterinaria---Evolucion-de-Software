package com.veterinaria.veterinariaapp.repository;

import com.veterinaria.veterinariaapp.config.Db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardRepository implements IDashboardRepository {

    @Override
    public int countCitas() throws Exception {
        final String sql = "SELECT COUNT(*) FROM citas";
        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    @Override
    public int countMascotas() throws Exception {
        final String sql = "SELECT COUNT(*) FROM mascota";
        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    @Override
    public int countClientes() throws Exception {
        final String sql = "SELECT COUNT(*) FROM cliente";
        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    @Override
    public Map<String, Integer> citasPorDia(int diasBack, int diasForward) throws Exception {
        final String sql = """
            SELECT DATE(fecha_hora) d, COUNT(*) c
            FROM citas
            WHERE fecha_hora >= DATE_SUB(CURDATE(), INTERVAL ? DAY)
              AND fecha_hora <  DATE_ADD(CURDATE(), INTERVAL ? DAY) + INTERVAL 1 DAY
            GROUP BY DATE(fecha_hora)
            ORDER BY d ASC
        """;

        Map<String,Integer> out = new LinkedHashMap<>();
        LocalDate hoy = LocalDate.now();
        LocalDate inicio = hoy.minusDays(diasBack);
        LocalDate fin    = hoy.plusDays(diasForward);

        for (LocalDate d = inicio; !d.isAfter(fin); d = d.plusDays(1)) {
            out.put(d.getDayOfMonth() + "/" + d.getMonthValue() + "/" + d.getYear(), 0);
        }

        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, diasBack);
            ps.setInt(2, diasForward);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocalDate d = rs.getDate("d").toLocalDate();
                    String k = d.getDayOfMonth() + "/" + d.getMonthValue() + "/" + d.getYear();
                    out.put(k, rs.getInt("c"));
                }
            }
        }
        return out;
    }

    @Override
    public Map<String, Integer> citasPorSemana(int semanasBack, int semanasForward) throws Exception {
        final String sql = """
            SELECT YEARWEEK(fecha_hora, 1) AS semana, COUNT(*) AS total
            FROM citas
            WHERE fecha_hora >= DATE_SUB(CURDATE(), INTERVAL ? WEEK)
              AND fecha_hora <  DATE_ADD(CURDATE(), INTERVAL ? WEEK) + INTERVAL 1 WEEK
            GROUP BY YEARWEEK(fecha_hora, 1)
            ORDER BY semana ASC
        """;

        Map<String, Integer> out = new LinkedHashMap<>();

        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, semanasBack);
            ps.setInt(2, semanasForward);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int yearWeek = rs.getInt("semana");
                    int year = yearWeek / 100;
                    int week = yearWeek % 100;
                    String label = "Semana " + week + " (" + year + ")";
                    out.put(label, rs.getInt("total"));
                }
            }
        }
        return out;
    }

    @Override
    public Map<String, Integer> distribucionEspecies(int topN) throws Exception {
        final String sql = """
            SELECT especie, COUNT(*) c
            FROM mascota
            WHERE especie IS NOT NULL AND TRIM(especie) <> ''
            GROUP BY especie
            ORDER BY c DESC
        """;

        Map<String,Integer> out = new LinkedHashMap<>();
        int otros = 0;
        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int idx = 0;
            while (rs.next()) {
                String esp = rs.getString("especie");
                int c = rs.getInt("c");
                if (idx < topN) out.put(esp, c);
                else otros += c;
                idx++;
            }
        }
        if (otros > 0) out.put("Otros", otros);
        return out;
    }
}
