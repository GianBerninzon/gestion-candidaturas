package com.gestion_candidaturas.gestion_candidaturas.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;

import java.util.Collections;
import java.util.UUID;


/**
 * Entidad que representa un usuario del sistema.
 *
 * @see RF-10: Permitir el registro y autenticación de usuarios.
 * @see RF-11: Control de acceso basado en roles.
 */
@Entity
@Table(name = "users")
public class User implements UserDetails {

    /**
     * Identificador único del usuario.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    /**
     * Nombre de usuario para autenticación.
     * @see RF-10: Credenciales de usuario para acceso al sistema.
     */
    @Column(unique = true, nullable = false)
    private String username;

    /**
     * Contraseña de usuario para autenticación.
     * @see RF-10: Credenciales de usuario para acceso al sistema.
     * @see RNF-11: Debe almacenarse de forma segura (encriptada).
     */
    @Column(nullable = false)
    private String password;

    /**
     * Correo electrónico del usuario.
     * @see RF-10: Información básica del usuario.
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * Rol del usuario que determina sus permisos en el sistema.
     * @see RF-11: Control de acceso basado en roles (USER, ADMIN, ROOT).
     */
    @Enumerated(EnumType.STRING)
    private Role role;

    /**
     * Determina si la cuenta de usuario está activa.
     */
    @Column(nullable = false)
    private boolean activo = true;


    /**
     * Fecha de creación del registro.
     */
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime fechaCreacion;



    // Constructor por defecto requerido por JPA
    public User() {
    }

    // Constructor con todos los campos principales
    public User(UUID id, String username, String password, String email, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    /**
     * Verifica si el usuario tiene un rol específico.
     * Este método es útil para las verificaciones de seguridad en los controladores.
     *
     * @param roleName Nombre del rol a verificar (sin el prefijo "ROLE_")
     * @return true si el usuario tiene ese rol, false en caso contrario
     */
    public boolean hasRole(String roleName){
        // Obtenemos las autoridades del usuario
//        Collection<? extends GrantedAuthority> authorities = getAuthorities();

        // Verificamos si alguna autoridad coincide con el rol especificado
        // Agregamos el prefijo "ROLE_" porque Spring Security utiliza este formato
//        return authorities.stream().map(GrantedAuthority::getAuthority)
//                .anyMatch(authority -> authority.equals("ROLE_" + roleName));

        // Alternativa más directa usando el enum Role:
        return this.role != null && this.role.name().equals(roleName);
    }

    // Implementación de métodos UserDetails
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_"+ role));
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
