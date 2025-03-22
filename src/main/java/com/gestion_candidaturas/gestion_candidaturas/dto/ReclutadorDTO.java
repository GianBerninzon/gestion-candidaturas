package com.gestion_candidaturas.gestion_candidaturas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * DTO para operaciones con reclutadores.
 */
public class ReclutadorDTO {

    /**
     * ID del reclutador (solo para actualización).
     */
    private UUID id;

    /**
     * ID de la empresa asociada al reclutador.
     */
    @NotNull(message = "El ID de la empresa es obligatorio")
    private UUID empresaId;

    /**
     * Nombre del reclutador.
     */
    @NotBlank(message = "El nombre del reclutador es obligatorio")
    private String nombre;

    /**
     * URL del perfil de LinkedIn del reclutador.
     */
    private String linkinUrl;

    // Constructores
    public ReclutadorDTO() {
    }

    public ReclutadorDTO(UUID id, UUID empresaId, String nombre, String linkinUrl) {
        this.id = id;
        this.empresaId = empresaId;
        this.nombre = nombre;
        this.linkinUrl = linkinUrl;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(UUID empresaId) {
        this.empresaId = empresaId;
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
