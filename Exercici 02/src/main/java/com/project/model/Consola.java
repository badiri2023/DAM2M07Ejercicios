package com.project.model;

import com.google.gson.annotations.SerializedName;

public class Consola {
    
    @SerializedName("name")
    private String nombre;
    
    @SerializedName("date")
    private String fecha; 
    
    private String procesador;
    
    private String color;
    
    @SerializedName("units_sold")
    private String unidadesVendidas;
    
    @SerializedName("image")
    private String imagen;

    // --- Getters ---
    public String getNombre() { return nombre; }
    public String getFecha() { return fecha; }
    public String getProcesador() { return procesador; }
    public String getColor() { return color; }
    public String getUnidadesVendidas() { return unidadesVendidas; }
    public String getImagen() { return imagen; }
}