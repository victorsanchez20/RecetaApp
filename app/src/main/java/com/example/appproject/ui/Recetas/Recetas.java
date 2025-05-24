package com.example.appproject.ui.Recetas;

public class Recetas {
    String idreceta;
    String nombre;
    String imagen;
    String video; /*nuevo*/
    String tiempo_preparacion;
    String dificultad;
    String categoria;
    double precio;
    String fecha_publicacion;

    // Constructor
    public Recetas(String idreceta, String dificultad, String categoria, String nombre,
                   String imagen, String video /*nuevo*/,double precio, String tiempo_preparacion, String fecha_publicacion) {
        this.idreceta = idreceta;
        this.nombre = nombre;
        this.imagen = imagen;
        this.video = video; /*nuevo*/
        this.tiempo_preparacion = tiempo_preparacion;
        this.dificultad = dificultad;
        this.categoria = categoria;
        this.precio = precio;
        this.fecha_publicacion = fecha_publicacion;
    }

    // Getters y setters
    public String getIdreceta() {
        return idreceta;
    }
    public void setIdreceta(String idreceta) {
        this.idreceta = idreceta;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getImagen() {
        return imagen;
    }
    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
    public String getTiempo_preparacion() {
        return tiempo_preparacion;
    }
    public void setTiempo_preparacion(String tiempo_preparacion) {
        this.tiempo_preparacion = tiempo_preparacion;
    }
    public String getDificultad() {
        return dificultad;
    }
    public void setDificultad(String dificultad) {
        this.dificultad = dificultad;
    }
    public String getCategoria() {
        return categoria;
    }
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    public double getPrecio() {
        return precio;
    }
    public void setPrecio(double precio) {
        this.precio = precio;
    }
    public String getFecha_publicacion() {
        return fecha_publicacion;
    }
    public void setFecha_publicacion(String fecha_publicacion) {
        this.fecha_publicacion = fecha_publicacion;
    }

    public String getVideo() {
        return video;
    }
    public void setVideo(String video) {
        this.video = video;
    }
}
