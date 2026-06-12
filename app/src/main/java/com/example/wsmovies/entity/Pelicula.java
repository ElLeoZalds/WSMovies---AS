package com.example.wsmovies.entity;

public class Pelicula {
    private int idpelicula;
    private String titulo;
    private String duracionmin;
    private String genero;
    private String alanzamiento;

    public Pelicula() {
    }

    public Pelicula(int idpelicula, String titulo, String duracionmin, String genero, String alanzamiento) {
        this.idpelicula = idpelicula;
        this.titulo = titulo;
        this.duracionmin = duracionmin;
        this.genero = genero;
        this.alanzamiento = alanzamiento;
    }

    public int getIdpelicula() {
        return idpelicula;
    }

    public void setIdpelicula(int idpelicula) {
        this.idpelicula = idpelicula;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDuracionmin() {
        return duracionmin;
    }

    public void setDuracionmin(String duracionmin) {
        this.duracionmin = duracionmin;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getAlanzamiento() {
        return alanzamiento;
    }

    public void setAlanzamiento(String alanzamiento) {
        this.alanzamiento = alanzamiento;
    }

}
