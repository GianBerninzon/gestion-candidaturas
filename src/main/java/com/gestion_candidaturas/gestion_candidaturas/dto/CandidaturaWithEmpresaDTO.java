package com.gestion_candidaturas.gestion_candidaturas.dto;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.gestion_candidaturas.gestion_candidaturas.model.EstadoCandidatura;

/**
 * DTO para representar una candidatura en las respuestas de la API.
 * Incluye informacion basica de la empresa.
 */
public class CandidaturaWithEmpresaDTO {
    private UUID id;
    private String cargo;
    private Date fecha;
    private EstadoCandidatura estado;
    private String notas;
    private UserResumenDTO userInfo;

    // Informacion basica de la empresa
    private EmpresaDTO empresa;

    // IDs de reclutadores asociados
    private Set<UUID> reclutadoresIds = new HashSet<>();

    // Constructores
    public CandidaturaWithEmpresaDTO() {
    }

    public CandidaturaWithEmpresaDTO(UUID id, String cargo, Date fecha, EstadoCandidatura estado, String notas, EmpresaDTO empresa) {
        this.id = id;
        this.cargo = cargo;
        this.fecha = fecha;
        this.estado = estado;
        this.notas = notas;
        this.empresa = empresa;
    }

    // Getters y Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public EmpresaDTO getEmpresa() {
        return empresa;
    }

    public void setEmpresa(EmpresaDTO empresa) {
        this.empresa = empresa;
    }

    public Set<UUID> getReclutadoresIds() {
        return reclutadoresIds;
    }

    public void setReclutadoresIds(Set<UUID> reclutadoresIds) {
        this.reclutadoresIds = reclutadoresIds;
    }

    public UserResumenDTO getUserInfo() {
        return userInfo;
    }
    
    public void setUserInfo(UserResumenDTO userInfo) {
        this.userInfo = userInfo;
    }

}
