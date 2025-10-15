package com.veterinaria.veterinariaapp.model;

public class Usuario {
    private int id;
    private String nombre;
    private String email;
    private String passHash;
    private String rol;   // nombre del rol (ADMIN/VETERINARIO/RECEPCION)
    private int rolId;    // id de roles.id (para guardar en BD)
    private boolean activo;

    // getters/setters
    public int getId(){ return id; }
    public void setId(int id){ this.id = id; }

    public String getNombre(){ return nombre; }
    public void setNombre(String nombre){ this.nombre = nombre; }

    public String getEmail(){ return email; }
    public void setEmail(String email){ this.email = email; }

    public String getPassHash(){ return passHash; }
    public void setPassHash(String passHash){ this.passHash = passHash; }

    public String getRol(){ return rol; }
    public void setRol(String rol){ this.rol = rol; }

    public int getRolId(){ return rolId; }
    public void setRolId(int rolId){ this.rolId = rolId; }

    public boolean isActivo(){ return activo; }
    public void setActivo(boolean activo){ this.activo = activo; }
}
