package com.gestion_candidaturas.gestion_candidaturas.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para recibir solicitudes de autenticación.
 */
public class AuthRequest {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    // Constructor por defecto
    public AuthRequest() {
    }

    // Constructor con todos los campos
    public AuthRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
