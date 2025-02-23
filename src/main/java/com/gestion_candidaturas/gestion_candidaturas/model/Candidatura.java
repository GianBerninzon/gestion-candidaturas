package com.gestion_candidaturas.gestion_candidaturas.model;


import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "candidaturas")
public class Candidatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; //relacion con el usuario

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    private String cargo;
    private Date fecha;

    @Enumerated(EnumType.STRING)
    private EstadoCandidatura estado;

    private String notas;

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
