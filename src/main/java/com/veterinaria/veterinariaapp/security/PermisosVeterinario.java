package com.veterinaria.veterinariaapp.security; // o .permissions

import com.veterinaria.veterinariaapp.ui.MainWindow;
import javax.swing.*;

public class PermisosVeterinario implements IPermisosRol {

    @Override
    public void configurarPermisos(MainWindow mainWindow) {
        // Veterinario: Citas y Mascotas

        // Menú Superior
        mainWindow.getMiClientes().setVisible(false); // Oculta Clientes
        mainWindow.getMiMascotas().setVisible(true);  // Muestra Mascotas
        mainWindow.getMiCitas().setVisible(true);     // Muestra Citas
        mainWindow.getMiUsuarios().setVisible(false); // Oculta Usuarios
        mainWindow.getMiPagos().setVisible(false);

        // Menú Lateral
        mainWindow.getBtnClientes().setVisible(false); // Oculta Clientes
        mainWindow.getBtnMascotas().setVisible(true);  // Muestra Mascotas
        mainWindow.getBtnCitas().setVisible(true);     // Muestra Citas
        mainWindow.getBtnUsuarios().setVisible(false); // Oculta Usuarios
        
        mainWindow.getBtnHistorial().setVisible(true); // Historial
        mainWindow.getMiHistorial().setVisible(true);  //Historial
        mainWindow.getBtnPagos().setVisible(false);
    }
}