package com.veterinaria.veterinariaapp.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Db {
    // CORREGIDO: Se agregó &allowPublicKeyRetrieval=true al final
    private static final String URL  =
        "jdbc:mysql://localhost:3306/veterinaria_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASS = "root"; 

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // Test rápido
    public static void main(String[] args) {
        try (Connection cn = getConnection()) {
            System.out.println("OK DB: " + cn.getMetaData().getURL());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}