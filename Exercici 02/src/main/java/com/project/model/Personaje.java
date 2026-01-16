package com.project.model;

import com.google.gson.annotations.SerializedName;

public class Personaje {
    
    @SerializedName("name")
    private String nombre;
    
    @SerializedName("image")
    private String imagen; // El nombre del archivo (ej. "mario.png")
    
    private String color;
    
    @SerializedName("game")
    private String juego; // El juego al que pertenece

    // --- Getters (métodos para obtener los datos) ---
    public String getNombre() { return nombre; }
    public String getImagen() { return imagen; }
    public String getColor() { return color; }
    public String getJuego() { return juego; }
}