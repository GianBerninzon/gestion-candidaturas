package com.gestion_candidaturas.gestion_candidaturas.dto;

import java.util.List;
import java.util.UUID;

public class EmpresaWithCandidaturasDTO {
    private UUID id;
    private String nombre;
    private String correo;
    private String telefono;
    private List<CandidaturaDTO> candidaturas;

    // Constructor vacío
    public EmpresaWithCandidaturasDTO() {}

    // Getters y setters
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

    public List<CandidaturaDTO> getCandidaturas() {
        return candidaturas;
    }

    public void setCandidaturas(List<CandidaturaDTO> candidaturas) {
        this.candidaturas = candidaturas;
    }

}
