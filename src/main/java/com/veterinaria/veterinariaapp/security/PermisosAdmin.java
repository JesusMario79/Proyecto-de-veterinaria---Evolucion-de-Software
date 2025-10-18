package com.veterinaria.veterinariaapp.security;

import com.veterinaria.veterinariaapp.ui.MainWindow;
import javax.swing.*; // Para JMenuItem y JButton

public class PermisosAdmin implements IPermisosRol {

    @Override
    public void configurarPermisos(MainWindow mainWindow) {
        // El Admin ve TODO, así que nos aseguramos de que todo esté visible.
        // (Aunque por defecto ya lo estén, es bueno ser explícito)

        // Menú Superior
        mainWindow.getMiClientes().setVisible(true);
        mainWindow.getMiMascotas().setVisible(true);
        mainWindow.getMiCitas().setVisible(true);
        mainWindow.getMiUsuarios().setVisible(true); // El Admin sí ve Usuarios

        // Menú Lateral
        mainWindow.getBtnClientes().setVisible(true);
        mainWindow.getBtnMascotas().setVisible(true);
        mainWindow.getBtnCitas().setVisible(true);
        mainWindow.getBtnUsuarios().setVisible(true); // El Admin sí ve Usuarios
    }
}