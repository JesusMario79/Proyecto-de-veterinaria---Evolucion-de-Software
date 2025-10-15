package com.veterinaria.veterinariaapp;

import com.veterinaria.veterinariaapp.ui.LoginFrame;
import javax.swing.SwingUtilities;


//clase veterinario
public class VeterinariaApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
