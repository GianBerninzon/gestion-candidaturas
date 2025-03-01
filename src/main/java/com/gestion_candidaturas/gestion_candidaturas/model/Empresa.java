package com.gestion_candidaturas.gestion_candidaturas.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


/**
 * Entidad que representa una empresa en el sistema.
 *
 * @see RF-08: Permitir agregar correos electrónicos de contacto de la empresa.
 * @see RF-09: Permitir agregar teléfono de la empresa.
 */
@Entity
@Table(name = "empresas")
public class Empresa {

    /**
     * Identificador único de la empresa.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    /**
     * Nombre de la empresa.
     * @see RF-08: Información básica de la empresa.
     */
    private String nombre;

    /**
     * Correo electrónico de contacto de la empresa.
     * @see RF-08: Información de contacto de la empresa.
     */
    private String correo;

    /**
     * Teléfono principal de la empresa.
     * @see RF-09: Permitir agregar teléfono principal de la empresa.
     */
    @ElementCollection
    private String telefono;

    /**
     * Fecha de creación del registro.
     */
    @Column(name = "fecha_cracion", nullable = false,updatable = false)
    @CreationTimestamp
    private LocalDate fechaCracion;

    /**
     * Fecha de última actualización del registro.
     */
    @Column(name = "fecha_actualizacion")
    @UpdateTimestamp
    private LocalDateTime fechaActualizacion;

    /**
     * Relación con reclutadores asociados a la empresa.
     */
    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Reclutador> reclutadores = new HashSet<>();

    /**
     * Relación con candidaturas asociadas a la empresa.
     */
    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Candidatura> candidaturas = new HashSet<>();

    public Empresa() {
    }

    public Empresa(UUID id, String nombre, String correo, String telefono, LocalDate fechaCracion, LocalDateTime fechaActualizacion, Set<Reclutador> reclutadores, Set<Candidatura> candidaturas) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.fechaCracion = fechaCracion;
        this.fechaActualizacion = fechaActualizacion;
        this.reclutadores = reclutadores;
        this.candidaturas = candidaturas;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public LocalDate getFechaCracion() {
        return fechaCracion;
    }

    public void setFechaCracion(LocalDate fechaCracion) {
        this.fechaCracion = fechaCracion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public Set<Reclutador> getReclutadores() {
        return reclutadores;
    }

    public void setReclutadores(Set<Reclutador> reclutadores) {
        this.reclutadores = reclutadores;
    }

    public Set<Candidatura> getCandidaturas() {
        return candidaturas;
    }

    public void setCandidaturas(Set<Candidatura> candidaturas) {
        this.candidaturas = candidaturas;
    }
}
