package com.gestion_candidaturas.gestion_candidaturas.model;

import jakarta.persistence.*;

@Entity
@Table(name = "reclutadores")
public class Reclutador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    private String nombre;
    private String linkinUrl;

    public Reclutador() {
    }

    public Reclutador(Empresa empresa, String nombre, String linkinUrl) {
        this.empresa = empresa;
        this.nombre = nombre;
        this.linkinUrl = linkinUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getLinkinUrl() {
        return linkinUrl;
    }

    public void setLinkinUrl(String linkinUrl) {
        this.linkinUrl = linkinUrl;
    }
}
