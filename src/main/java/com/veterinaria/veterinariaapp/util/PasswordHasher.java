package com.veterinaria.veterinariaapp.util;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordHasher {
    private PasswordHasher() {}

    // Genera un hash (lo usarás para insertar/actualizar en la BD)
    public static String hash(String plain) {
        return BCrypt.hashpw(plain, BCrypt.gensalt(10));
    }

    // Verifica (se usa en el login)
    public static boolean verify(String plain, String hash) {
        return plain != null && hash != null && !hash.isBlank()
                && BCrypt.checkpw(plain, hash);
    }

    // <-- ESTE main es el que vas a ejecutar con Shift+F6
    public static void main(String[] args) {
        String password = "admin123"; // PON AQUÍ la clave que quieres usar
        String hash = hash(password);
        System.out.println("HASH: " + hash);
    }
}
