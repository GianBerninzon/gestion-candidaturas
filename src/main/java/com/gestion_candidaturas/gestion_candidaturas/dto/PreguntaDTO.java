package com.gestion_candidaturas.gestion_candidaturas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * DTO para operaciones con preguntas.
 * Simplifica la estructura para evitar problemas de serialización.
 */
public class PreguntaDTO {
    /**
     * ID de la pregunta (solo para actualización).
     */
    private UUID id;

    /**
     * ID de la candidatura a la que está asociada la pregunta.
     */
    @NotNull(message = "El ID de la candidatura es obligatorio")
    private UUID candidaturaId;

    /**
     * Texto de la pregunta.
     */
    @NotBlank(message = "El texto de la pregunta es obligatorio")
    private String pregunta;

    // Constructores
    public PreguntaDTO() {
    }

    public PreguntaDTO(UUID id, UUID candidaturaId, String pregunta) {
        this.id = id;
        this.candidaturaId = candidaturaId;
        this.pregunta = pregunta;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCandidaturaId() {
        return candidaturaId;
    }

    public void setCandidaturaId(UUID candidaturaId) {
        this.candidaturaId = candidaturaId;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }
}
