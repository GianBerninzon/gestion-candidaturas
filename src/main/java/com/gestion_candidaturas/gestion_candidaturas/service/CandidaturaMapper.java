package com.gestion_candidaturas.gestion_candidaturas.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.gestion_candidaturas.gestion_candidaturas.dto.CandidaturaDTO;
import com.gestion_candidaturas.gestion_candidaturas.dto.CandidaturaWithEmpresaDTO;
import com.gestion_candidaturas.gestion_candidaturas.dto.EmpresaDTO;
import com.gestion_candidaturas.gestion_candidaturas.dto.PageResponseDTO;
import com.gestion_candidaturas.gestion_candidaturas.dto.ReclutadorDTO;
import com.gestion_candidaturas.gestion_candidaturas.dto.UserResumenDTO;
import com.gestion_candidaturas.gestion_candidaturas.model.Candidatura;
import com.gestion_candidaturas.gestion_candidaturas.model.Empresa;
import com.gestion_candidaturas.gestion_candidaturas.model.Reclutador;

/**
 * Servicio para mapear entidades de Candidatura a DTOs.
 */
@Service
public class CandidaturaMapper {

    /**
     * Convierte una entidad Candidatura a un DTO de respuesta.
     * Incluye informacion basica de la empresa.
     * 
     * @param candidatura La entidad Candidatura
     * @return DTO con la informacion de la candidatura y su empresa
     */
    public  CandidaturaWithEmpresaDTO toResponseDTO(Candidatura candidatura) {
        if(candidatura == null){
            return null;
        }   

        CandidaturaWithEmpresaDTO dto = new CandidaturaWithEmpresaDTO();
        dto.setId(candidatura.getId());
        dto.setCargo(candidatura.getCargo());
        dto.setFecha(candidatura.getFecha());
        dto.setEstado(candidatura.getEstado());
        dto.setNotas(candidatura.getNotas());

        // Mapear informacion de la empresa si existe
        if(candidatura.getEmpresa() != null){
            dto.setEmpresa(toEmpresaDTO(candidatura.getEmpresa()));
        }

        // Mapear IDs de reclutadores si existen
        if(candidatura.getReclutadores() != null && !candidatura.getReclutadores().isEmpty()){
            dto.setReclutadoresIds(
                candidatura.getReclutadores().stream()
                .map(Reclutador::getId)
                .collect(Collectors.toSet())
            );
        }

        // Mapear informacion basica del usuario si existe
        if(candidatura.getUser() != null){
            UserResumenDTO userInfo = new UserResumenDTO();
            userInfo.setId(candidatura.getUser().getId());
            userInfo.setUsername(candidatura.getUser().getUsername());
            userInfo.setNumeroCandidaturas(0);

            dto.setUserInfo(userInfo);
        }

        return dto;
    }

    /**
     * Convierte una entidad Empresa a un DTO.
     * 
     * @param empresa la entidad Empresa
     * @return DTO con la informacion basica de la empresa
     */
    public EmpresaDTO toEmpresaDTO(Empresa empresa){
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
     * Convierte una lista de entidades Candidatura a una lista de DTOs
     * 
     * @param candidaturas Lista de entidad Candidatura
     * @return Lista de DTOs de respuesta
     */
    public List<CandidaturaWithEmpresaDTO> toResponseDTOList(List<Candidatura> candidaturas){
        return candidaturas.stream()
        .map(this::toResponseDTO)
        .collect(Collectors.toList());
    }

    /**
     * Convierte una pagina de entidades Candidatura a un DTO de respuesta paginada.
     * 
     * @param candidaturasPage Pagina de entidades Candidatura
     * @return DTO de respuesta paginada con los DTOs de candidaturas
     */
    public PageResponseDTO<CandidaturaWithEmpresaDTO> toPageResponseDTO(Page<Candidatura> candidaturasPage){
        //Page<CandidaturaWithEmpresaDTO> dtoPage=candidaturasPage.map(this::toResponseDTO);

        //return new PageResponseDTO<>(dtoPage);

        return new PageResponseDTO<>(candidaturasPage, this::toResponseDTO);
    }

    public CandidaturaDTO toDTO(Candidatura candidatura){
        CandidaturaDTO dto = new CandidaturaDTO();
        dto.setId(candidatura.getId());
        dto.setCargo(candidatura.getCargo());
        dto.setEstado(candidatura.getEstado());
        dto.setNotas(candidatura.getNotas());
        dto.setFecha(candidatura.getFecha());

        //Incluir información del usuario
        if(candidatura.getUser() != null){
            UserResumenDTO userInfo = new UserResumenDTO();
            userInfo.setId(candidatura.getUser().getId());
            userInfo.setUsername(candidatura.getUser().getUsername());
            dto.setUserInfo(userInfo);
        }

        // Incluir información de los reclutadores
        if(candidatura.getReclutadores() != null && !candidatura.getReclutadores().isEmpty()){
            List<ReclutadorDTO> reclutadoresDTO = candidatura.getReclutadores().stream()
                .map(reclutador -> {
                    ReclutadorDTO recDTO = new ReclutadorDTO();
                    recDTO.setId(reclutador.getId());
                    recDTO.setNombre(reclutador.getNombre());
                    recDTO.setLinkinUrl(reclutador.getLinkinUrl());
                    return recDTO;
                })
                .collect(Collectors.toList());
            dto.setReclutadores(reclutadoresDTO);
        }

        return dto;
    }


}
