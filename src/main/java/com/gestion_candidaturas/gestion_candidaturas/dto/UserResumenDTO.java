package com.gestion_candidaturas.gestion_candidaturas.dto;

import java.util.UUID;

/**
 * DTO que contiene información resumida de un usuario, excluyendo datos sensibles.
 * Se utiliza para incluir información básica de usuarios en otros DTOs.
 */
public class UserResumenDTO {

    /**
     * Identificador único del usuario.
     */
    private UUID id;

    /**
     * Nombre de usuario (no sensible).
     */
    private String username;

    /**
     * Número de candidaturas que tiene asociadas a una empresa específica.
     */
    private int numeroCandidaturas;

    public UserResumenDTO() {
    }

    public UserResumenDTO(UUID id, String username, int numeroCandidaturas) {
        this.id = id;
        this.username = username;
        this.numeroCandidaturas = numeroCandidaturas;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getNumeroCandidaturas() {
        return numeroCandidaturas;
    }

    public void setNumeroCandidaturas(int numeroCandidaturas) {
        this.numeroCandidaturas = numeroCandidaturas;
    }
}
