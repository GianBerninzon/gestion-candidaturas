package com.gestion_candidaturas.gestion_candidaturas.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entidad que representa una candidatura a un puesto de trabajo.
 *
 * @see RF-01: Permitir registrar una candidatura con información de empresa, cargo, fecha y estado.
 * @see RF-02: Permitir actualizar el estado de una candidatura.
 * @see RF-03: Permitir buscar y filtrar candidaturas por diversos criterios.
 * @see RF-05: Relacionar reclutadores con candidaturas.
 */
@Entity
@Table(name = "candidaturas")
public class Candidatura {

    /**
     * Identificador único de la candidatura.
     */
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    /**
     * Usuario propietario de la candidatura.
     * @see RF-11: Control de acceso (cada usuario ve sus propias candidaturas).
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference("user-candidaturas")
    private User user; //relacion con el usuario

    /**
     * Empresa a la que se aplica.
     * @see RF-01: Información de empresa para cada candidatura.
     */
    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    @JsonBackReference("empresa-candidaturas")
    private Empresa empresa;

    /**
     * Puesto o cargo al que se aplica.
     * @see RF-01: Incluir cargo en la información de candidatura.
     */
    private String cargo;

    /**
     * Fecha de aplicación a la candidatura.
     * @see RF-01: Registro de la fecha de aplicación.
     */
    private Date fecha;

    /**
     * Estado actual de la candidatura.
     * @see RF-02: Permitir actualizar el estado de una candidatura.
     */
    @Enumerated(EnumType.STRING)
    private EstadoCandidatura estado;

    /**
     * Notas adicionales sobre la candidatura.
     * @see RF-01: Permitir agregar observaciones a cada candidatura.
     */
    private String notas;

    /**
     * Relación con reclutadores asociados a la candidatura.
     * @see RF-05: Relacionar reclutadores con candidaturas.
     */
    @ManyToMany
    @JoinTable(
            name = "candidatura_reclutador",
            joinColumns = @JoinColumn(name = "candidatura_id"),
            inverseJoinColumns = @JoinColumn(name = "reclutador_id")
    )
    @JsonManagedReference("candidatura-reclutadores")
    private Set<Reclutador> reclutadores = new HashSet<>();

    public Candidatura() {
    }

    public Candidatura(User user, Empresa empresa, String cargo, Date fecha, EstadoCandidatura estado, String notas) {
        this.user = user;
        this.empresa = empresa;
        this.cargo = cargo;
        this.fecha = fecha;
        this.estado = estado;
        this.notas = notas;
    }

    public Candidatura(UUID id, User user, Empresa empresa, String cargo, Date fecha, EstadoCandidatura estado, String notas, Set<Reclutador> reclutadores) {
        this.id = id;
        this.user = user;
        this.empresa = empresa;
        this.cargo = cargo;
        this.fecha = fecha;
        this.estado = estado;
        this.notas = notas;
        this.reclutadores = reclutadores;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Set<Reclutador> getReclutadores() {
        return reclutadores;
    }

    public void setReclutadores(Set<Reclutador> reclutadores) {
        this.reclutadores = reclutadores;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public EstadoCandidatura getEstado() {
        return estado;
    }

    public void setEstado(EstadoCandidatura estado) {
        this.estado = estado;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
