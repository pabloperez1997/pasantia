package com.example.backgroundeditor;

public class Fondo {

    private String nombre;
    private String url;

    public Fondo(String nombre, String url) {

        this.nombre = nombre;
        this.url = url;
    }



    public String getNombre() {
        return this.nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getURL() {
        return this.url;
    }
    public void setURL(String url) {
        this.url = url;
    }





}
