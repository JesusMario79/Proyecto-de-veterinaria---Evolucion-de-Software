package com.veterinaria.veterinariaapp.security; // o .permissions

import com.veterinaria.veterinariaapp.ui.MainWindow;
import javax.swing.*;

public class PermisosRecepcionista implements IPermisosRol {

    @Override
    public void configurarPermisos(MainWindow mainWindow) {
        // Recepcionista: Citas y Clientes

        // Menú Superior
        mainWindow.getMiClientes().setVisible(true);  // Muestra Clientes
        mainWindow.getMiMascotas().setVisible(false); // Oculta Mascotas
        mainWindow.getMiCitas().setVisible(true);     // Muestra Citas
        mainWindow.getMiUsuarios().setVisible(false); // Oculta Usuarios

        // Menú Lateral
        mainWindow.getBtnClientes().setVisible(true);  // Muestra Clientes
        mainWindow.getBtnMascotas().setVisible(false); // Oculta Mascotas
        mainWindow.getBtnCitas().setVisible(true);     // Muestra Citas
        mainWindow.getBtnUsuarios().setVisible(false); // Oculta Usuarios
    }
}