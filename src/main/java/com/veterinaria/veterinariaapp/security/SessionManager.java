package com.veterinaria.veterinariaapp.security;

import com.veterinaria.veterinariaapp.model.Usuario;

public class SessionManager {
    private static final SessionManager INSTANCE = new SessionManager();
    private Usuario currentUser;
    private SessionManager(){}

    public static SessionManager get(){ return INSTANCE; }
    public void login(Usuario u){ currentUser = u; }
    public void logout(){ currentUser = null; }
    public boolean isAuthenticated(){ return currentUser != null; }
    public Usuario getCurrentUser(){ return currentUser; }
}
