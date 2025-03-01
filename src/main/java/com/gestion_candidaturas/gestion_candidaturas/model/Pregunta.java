package com.gestion_candidaturas.gestion_candidaturas.model;

import jakarta.persistence.*;

import java.util.UUID;

/**
 * Entidad que representa una pregunta de entrevista asociada a una candidatura.
 *
 * @see RF-06: Permitir agregar preguntas de entrevista para una candidatura.
 * @see RF-07: Mostrar el número total de preguntas almacenadas para cada candidatura.
 */

@Entity
@Table(name = "preguntas")
public class Pregunta {

    /**
     * Identificador único de la pregunta.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    /**
     * Candidatura a la que está asociada la pregunta.
     * @see RF-06: Relación con la candidatura correspondiente.
     */
    @ManyToOne
    @JoinColumn(name = "candidatura_id", nullable = false)
    private Candidatura candidatura;

    /**
     * Texto de la pregunta de entrevista.
     * @see RF-06: Permitir agregar preguntas de entrevista.
     */
    private String pregunta;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private User usuario;

    public Pregunta() {
    }

    public Pregunta(UUID id, Candidatura candidatura, String pregunta, User usuario) {
        this.id = id;
        this.candidatura = candidatura;
        this.pregunta = pregunta;
        this.usuario = usuario;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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

    public User getUsuario() {
        return usuario;
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
    }
}
