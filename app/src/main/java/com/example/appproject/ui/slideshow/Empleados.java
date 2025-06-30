package com.example.appproject.ui.slideshow;

public class Empleados {
    int id;
    String NombreEmpleado;
    String ApellidoPat;
    String ApellidoMat;
    int PorcentajeEmpleado;
    String foto;


    public Empleados(int id, String nombreEmpleado, String apellidoPat, String apellidoMat,
                     int porcentajeEmpleado, String fotoE) {
        this.id = id;
        NombreEmpleado = nombreEmpleado;
        ApellidoPat = apellidoPat;
        ApellidoMat = apellidoMat;
        PorcentajeEmpleado = porcentajeEmpleado;
        foto = fotoE;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreEmpleado() {
        return NombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado) {
        NombreEmpleado = nombreEmpleado;
    }

    public String getApellidoPat() {
        return ApellidoPat;
    }

    public void setApellidoPat(String apellidoPat) {
        ApellidoPat = apellidoPat;
    }

    public String getApellidoMat() {
        return ApellidoMat;
    }

    public void setApellidoMat(String apellidoMat) {
        ApellidoMat = apellidoMat;
    }

    public int getPorcentajeEmpleado() {
        return PorcentajeEmpleado;
    }

    public void setPorcentajeEmpleado(int porcentajeEmpleado) {
        PorcentajeEmpleado = porcentajeEmpleado;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
