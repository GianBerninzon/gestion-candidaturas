package com.gestion_candidaturas.gestion_candidaturas.util;


import com.gestion_candidaturas.gestion_candidaturas.model.Role;
import com.gestion_candidaturas.gestion_candidaturas.model.User;
import com.gestion_candidaturas.gestion_candidaturas.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la utilidad JwtUtil.
 * Verifica la correcta generación y validación de tokens JWT.
 */
public class JwtUtilTest {

    private JwtUtil jwtUtil;
    private User testUser;

    @BeforeEach
    public void setUp() {
        jwtUtil = new JwtUtil();
        testUser = new User(
                UUID.randomUUID(),
                "testuser",
                "password",
                "test@example.com",
                Role.USER
        );
    }

    /**
     * Verifica que se pueda generar un token JWT válido para un usuario.
     */
    @Test
    public void shouldGenerateToken() {
        String token = jwtUtil.generateToken(testUser);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    /**
     * Verifica que se pueda extraer correctamente el nombre de usuario del token.
     */
    @Test
    public void shouldExtractUsername() {
        String token = jwtUtil.generateToken(testUser);
        String username = jwtUtil.extractUsername(token);

        assertEquals("testuser", username);
    }

    /**
     * Verifica que se pueda extraer correctamente la fecha de expiración del token.
     */
    @Test
    public void shouldExtractExpiration() {
        String token = jwtUtil.generateToken(testUser);
        Date expiration = jwtUtil.extractExpiration(token);

        assertNotNull(expiration);
        // La fecha de expiración debe ser en el futuro
        assertTrue(expiration.after(new Date()));
    }

    /**
     * Verifica que el token sea válido para el usuario que lo generó.
     */
    @Test
    public void shouldValidateTokenForCorrectUser() {
        String token = jwtUtil.generateToken(testUser);

        boolean isValid = jwtUtil.validateToken(token, testUser);

        assertTrue(isValid);
    }

    /**
     * Verifica que el token sea inválido para un usuario diferente.
     */
    @Test
    public void shouldNotValidateTokenForWrongUser() {
        String token = jwtUtil.generateToken(testUser);

        User anotherUser = new User(
                UUID.randomUUID(),
                "anotheruser",
                "password",
                "another@example.com",
                Role.USER
        );

        boolean isValid = jwtUtil.validateToken(token, anotherUser);

        assertFalse(isValid);
    }
}
