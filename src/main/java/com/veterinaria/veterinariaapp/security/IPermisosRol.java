package com.veterinaria.veterinariaapp.security; // o .permissions

import com.veterinaria.veterinariaapp.ui.MainWindow; // Necesita saber qué ventana configurar

/**
 * Interfaz (Contrato) para definir cómo se configuran los permisos
 * de la ventana principal según el rol del usuario.
 * Cumple con el Principio Abierto-Cerrado (OCP).
 */
public interface IPermisosRol {
    /**
     * Aplica las restricciones de visibilidad a los componentes
     * de la MainWindow según este rol específico.
     * @param mainWindow La instancia de la ventana principal a configurar.
     */
    void configurarPermisos(MainWindow mainWindow);
}