package com.gestion_candidaturas.gestion_candidaturas.dto;

import com.gestion_candidaturas.gestion_candidaturas.model.EstadoCandidatura;
import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * DTO para la actualización de candidaturas.
 * Simplifica la estructura para evitar problemas de serialización.
 */
public class CandidaturaDTO {
    
    private UUID id;
    /**
     * ID de la empresa a la que pertenece la candidatura.
     */
    @NotNull(message = "El ID de la empresa es obligatorio")
    private UUID empresaId;

    /**
     * Puesto o cargo al que se aplica.
     */
    private String cargo;

    /**
     * Fecha de aplicación a la candidatura.
     */
    private Date fecha;

    /**
     * Estado actual de la candidatura.
     */
    private EstadoCandidatura estado;

    /**
     * Notas adicionales sobre la candidatura.
     */
    private String notas;

    private UserResumenDTO userInfo;

    private List<ReclutadorDTO> reclutadores;

    // Constructores
    public CandidaturaDTO() {
    }

    public CandidaturaDTO(UUID id, UUID empresaId, String cargo, Date fecha, EstadoCandidatura estado, String notas) {
        this.id = id;
        this.empresaId = empresaId;
        this.cargo = cargo;
        this.fecha = fecha;
        this.estado = estado;
        this.notas = notas;
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

    public UserResumenDTO getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserResumenDTO userInfo) {
        this.userInfo = userInfo;
    }

    public List<ReclutadorDTO> getReclutadores() {
        return reclutadores;
    }

    public void setReclutadores(List<ReclutadorDTO> reclutadores) {
        this.reclutadores = reclutadores;
    }
}
