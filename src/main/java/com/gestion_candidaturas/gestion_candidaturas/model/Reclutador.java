package com.gestion_candidaturas.gestion_candidaturas.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entidad que representa un reclutador asociado a una empresa.
 *
 * @see RF-05: Permitir guardar nombres y enlaces a perfiles de LinkedIn de los reclutadores.
 */
@Entity
@Table(name = "reclutadores")
public class Reclutador {

    /**
     * Identificador único del reclutador.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    /**
     * Empresa a la que está asociado el reclutador.
     * @see RF-05: Relación con la empresa correspondiente.
     */
    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    /**
     * Nombre del reclutador.
     * @see RF-05: Información básica del reclutador.
     */
    private String nombre;

    /**
     * URL del perfil de LinkedIn del reclutador.
     * @see RF-05: Información de contacto del reclutador.
     */
    private String linkinUrl;

    /**
     * Relación con candidaturas asociadas al reclutador.
     * @see RF-05: Relacionar reclutadores con candidaturas.
     */
    @ManyToMany(mappedBy = "reclutadores")
    private Set<Candidatura> candidaturas = new HashSet<>();

    public Reclutador() {
    }

    public Reclutador(UUID id, Empresa empresa, String nombre, String linkinUrl, Set<Candidatura> candidaturas) {
        this.id = id;
        this.empresa = empresa;
        this.nombre = nombre;
        this.linkinUrl = linkinUrl;
        this.candidaturas = candidaturas;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Set<Candidatura> getCandidaturas() {
        return candidaturas;
    }

    public void setCandidaturas(Set<Candidatura> candidaturas) {
        this.candidaturas = candidaturas;
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
