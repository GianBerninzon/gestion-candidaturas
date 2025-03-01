package com.gestion_candidaturas.gestion_candidaturas.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion_candidaturas.gestion_candidaturas.dto.AuthRequest;
import com.gestion_candidaturas.gestion_candidaturas.dto.RegisterRequest;
import com.gestion_candidaturas.gestion_candidaturas.model.Role;
import com.gestion_candidaturas.gestion_candidaturas.model.User;
import com.gestion_candidaturas.gestion_candidaturas.security.JwtUtil;
import com.gestion_candidaturas.gestion_candidaturas.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de integración para el controlador de autenticación.
 * Verifica el correcto funcionamiento de los endpoints de registro, login y obtención de información del usuario.
 */
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtUtil jwtUtil;

    /**
     * Verifica que el registro de un nuevo usuario sea exitoso.
     */
    @Test
    public void registerShouldReturnCreatedWhenSuccessful() throws Exception{
        // Datos para el registro
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setPassword("password123");
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setRol("USER");

        // Configurar comportamiento del mock
        User newUser = new User(UUID.randomUUID(), "newuser", "encoded_password", "newuser@example.com", Role.USER);
        when(userService.existsByUsername("newuser")).thenReturn(false);
        when(userService.existsByEmail("newuser@example.com")).thenReturn(false);
        when(userService.register(anyString(), anyString(), anyString(), anyString())).thenReturn(newUser);

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Usuario registrado correctamente"))
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    /**
     * Verifica que el registro falle si el nombre de usuario ya existe.
     */
    @Test
    public void registerShouldReturnBadRequestWhenUsernameExists() throws Exception{
        // Datos para el registro
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("existinguser");
        registerRequest.setPassword("password123");
        registerRequest.setEmail("newuser@example.com");

        // Configurar comportamiento del mock
        when(userService.existsByUsername("existinguser")).thenReturn(true);

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El nombre de usuario ya está en uso"));
    }

    /**
     * Verifica que el inicio de sesión sea exitoso.
     */
    @Test
    public void loginShouldReturnTokenWhenCredentialsAreValid() throws Exception{
        // Datos para el login
        AuthRequest authRequest = new AuthRequest("testuser", "password123");

        // Configurar comportamiento de los mocks
        Authentication authentication = new UsernamePasswordAuthenticationToken("testuser", "password123");
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        User user = new User(UUID.randomUUID(), "testuser", "ecoded_password", "test@example.com", Role.USER);
        when(userService.getUserByUsername("testuser")).thenReturn(user);
        when(jwtUtil.generateToken(any())).thenReturn("valid_token");

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("valid_token"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.role").value("USER"));

    }

    /**
     * Verifica que el inicio de sesión falle con credenciales inválidas.
     */
    @Test
    public void loginShouldReturnUnauthorizedWhenCredentialsAreInvalid() throws Exception{
        // Datos para el login
        AuthRequest authRequest = new AuthRequest("testuser", "wrongpassword");

        // Configurar comportamiento de los mocks
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Credenciales inválidas"));
    }

    /**
     * Verifica que se obtenga correctamente la información del usuario autenticado.
     */
    @Test
    @WithMockUser(username = "testuser")
    public void getCurrentUserShouldReturnUserInfoWhenAuthenticated() throws Exception{
        // Configurar comportamiento de los mocks
        User user = new User(UUID.randomUUID(), "testuser", "encoded_password", "test@example.com", Role.USER);
        when(userService.getCurrentUser()).thenReturn(user);

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.rol").value("USER"));
    }
}
