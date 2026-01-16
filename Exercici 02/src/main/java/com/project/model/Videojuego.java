package com.project.model;

import com.google.gson.annotations.SerializedName;

public class Videojuego {
    
    @SerializedName("name")
    private String nombre;
    
    @SerializedName("year")
    private int ano; 
    
    @SerializedName("type")
    private String tipo;
    
    @SerializedName("plot")
    private String descripcion;
    
    @SerializedName("image")
    private String imagen;

    // --- Getters ---
    public String getNombre() { return nombre; }
    public int getAno() { return ano; }
    public String getTipo() { return tipo; }
    public String getDescripcion() { return descripcion; }
    public String getImagen() { return imagen; }
}