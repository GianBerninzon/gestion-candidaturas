package com.gestion_candidaturas.gestion_candidaturas.model;

import jakarta.persistence.*;

@Entity
@Table(name = "preguntas")
public class Pregunta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "candidatura_id", nullable = false)
    private Candidatura candidatura;

    private String pregunta;

    public Pregunta() {
    }

    public Pregunta(Candidatura candidatura, String pregunta) {
        this.candidatura = candidatura;
        this.pregunta = pregunta;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Candidatura getCandidatura() {
        return candidatura;
    }

    public void setCandidatura(Candidatura candidatura) {
        this.candidatura = candidatura;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }
}
