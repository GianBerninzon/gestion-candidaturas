package com.gestion_candidaturas.gestion_candidaturas.dto;

import java.util.List;
import java.util.UUID;

/**
 * Objeto de Transferencia de Datos (DTO) que representa una empresa junto con
 * información de los usuarios que tienen candidaturas asociadas a ella.
 * Este DTO se utiliza exclusivamente para administradores que necesitan ver
 * qué usuarios están aplicando a qué empresas.
 */
public class EmpresaWithUsersDTO {


    /**
     * Identificador único de la empresa.
     */
    private UUID id;

    /**
     * Nombre de la empresa.
     */
    private String nombre;

    /**
     * Información de contacto principal (correo).
     */
    private String correo;

    /**
     * Teléfono principal de la empresa.
     */
    private String telefono;

    /**
     * Lista de usuarios asociados a esta empresa a través de candidaturas.
     * Contiene solo información resumida de los usuarios, no datos sensibles.
     */
    private List<UserResumenDTO> usuariosAsociados;

    public EmpresaWithUsersDTO() {
    }

    public EmpresaWithUsersDTO(UUID id, String nombre, String correo, String telefono, List<UserResumenDTO> usuariosAsociados) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.usuariosAsociados = usuariosAsociados;
    }

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

    public List<UserResumenDTO> getUsuariosAsociados() {
        return usuariosAsociados;
    }

    public void setUsuariosAsociados(List<UserResumenDTO> usuariosAsociados) {
        this.usuariosAsociados = usuariosAsociados;
    }
}
