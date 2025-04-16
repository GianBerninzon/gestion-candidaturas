package com.gestion_candidaturas.gestion_candidaturas.dto;

import java.util.UUID;

/**
 * DTO para representar un reclutador en las respuesta de la API.
 * Incluye informacion basica de la empresa
 */
public class ReclutadorWithEmpresaDTO {
    private UUID id;
    private String nombre;
    private String linkinUrl;
    private EmpresaDTO empresa;

    // Constructores, getters y setters
    public ReclutadorWithEmpresaDTO() {
    }

    // Getters y Setters
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

    public String getLinkinUrl() {
        return linkinUrl;
    }

    public void setLinkinUrl(String linkinUrl) {
        this.linkinUrl = linkinUrl;
    }

    public EmpresaDTO getEmpresa() {
        return empresa;
    }

    public void setEmpresa(EmpresaDTO empresa) {
        this.empresa = empresa;
    }
}
