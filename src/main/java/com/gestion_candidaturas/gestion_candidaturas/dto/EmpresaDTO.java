package com.gestion_candidaturas.gestion_candidaturas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

/**
 * DTO para operaciones con empresas.
 * Simplifica la estructura para evitar problemas de serialización.
 */
public class EmpresaDTO {

    /**
     * Identificador único de la empresa.
     */
    private UUID id;

    /**
     * Nombre de la empresa.
     */
    @NotBlank(message = "El nombre de la empresa es obligatorio")
    private String nombre;

    /**
     * Correo electrónico de contacto de la empresa.
     */
    @Email(message = "Formato de correo electrónico inválido")
    private String correo;

    /**
     * Teléfono principal de la empresa.
     */
    private String telefono;

    // Constructores
    public EmpresaDTO() {
    }

    public EmpresaDTO(UUID id, String nombre, String correo, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
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
}
