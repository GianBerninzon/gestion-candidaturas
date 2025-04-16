package com.gestion_candidaturas.gestion_candidaturas.service;

import org.springframework.stereotype.Service;

import com.gestion_candidaturas.gestion_candidaturas.dto.EmpresaDTO;
import com.gestion_candidaturas.gestion_candidaturas.dto.ReclutadorWithEmpresaDTO;
import com.gestion_candidaturas.gestion_candidaturas.model.Empresa;
import com.gestion_candidaturas.gestion_candidaturas.model.Reclutador;

/**
 * Servicio para mapear entidades de Reclutador a DTOs
 */
@Service
public class ReclutadorMapper {

    /**
     * Convierte una entidad Reclutador a un DTO de respuesta.
     * Incluye información de la empresa.
     */
    public ReclutadorWithEmpresaDTO toResponseDTO(Reclutador reclutador){
        if(reclutador == null){
            return null;
        }

        ReclutadorWithEmpresaDTO dto = new ReclutadorWithEmpresaDTO();
        dto.setId(reclutador.getId());
        dto.setNombre(reclutador.getNombre());
        dto.setLinkinUrl(reclutador.getLinkinUrl());

        // Mapear informacion de la empresa si existe
        if(reclutador.getEmpresa() != null){
            dto.setEmpresa(toEmpresaDTO(reclutador.getEmpresa()));
        }

        return dto;
    }

    /**
     *  Convierte una entidad Empresa a un DTO.
     */
    private EmpresaDTO toEmpresaDTO(Empresa empresa){
        if(empresa == null){
            return null;
        }

        EmpresaDTO dto = new EmpresaDTO();
        dto.setId(empresa.getId());
        dto.setNombre(empresa.getNombre());
        dto.setCorreo(empresa.getCorreo());
        dto.setTelefono(empresa.getTelefono());
        return dto;
    }

    /**
     * Convierte una entidad Reclutador a ReclutadorWithEmpresaDTO.
     * Este metodo es utilizado especificamente para la lista de reclutadores.
     * 
     * @param relutador La entidad Reclutador a convertir
     * @return ReclutadorWithEmpresDTO con la informacion del reclutador y su empresa
     */
    public ReclutadorWithEmpresaDTO toReclutadorWithEmpresaDTO(Reclutador reclutador){
        if(reclutador == null){
            return null;
        }

        ReclutadorWithEmpresaDTO dto = new ReclutadorWithEmpresaDTO();
        dto.setId(reclutador.getId());
        dto.setNombre(reclutador.getNombre());
        dto.setLinkinUrl(reclutador.getLinkinUrl());

        // Mapear informacion de la empresa si existe
        if(reclutador.getEmpresa() != null){
            dto.setEmpresa(toEmpresaDTO(reclutador.getEmpresa()));
        }

        return dto;
    }

}
