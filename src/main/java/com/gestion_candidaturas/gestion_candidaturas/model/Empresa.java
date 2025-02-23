package com.gestion_candidaturas.gestion_candidaturas.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "empresas")
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String correo;

    @ElementCollection
    private List<String> telefonos;

    public Empresa() {
    }

    public Empresa(String nombre, String correo, List<String> telefonos) {
        this.nombre = nombre;
        this.correo = correo;
        this.telefonos = telefonos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public List<String> getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(List<String> telefonos) {
        this.telefonos = telefonos;
    }
}
