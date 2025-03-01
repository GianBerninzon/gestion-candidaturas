package com.gestion_candidaturas.gestion_candidaturas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion_candidaturas.gestion_candidaturas.model.*;
import com.gestion_candidaturas.gestion_candidaturas.security.JwtUtil;
import com.gestion_candidaturas.gestion_candidaturas.service.PreguntaService;
import com.gestion_candidaturas.gestion_candidaturas.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas para el controlador de preguntas de entrevista.
 * Verifica operaciones CRUD y control de acceso.
 */
@WebMvcTest(PreguntaController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PreguntaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PreguntaService preguntaService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtUtil jwtUtil;

    /**
     * Verifica que se puedan obtener preguntas por candidatura.
     */
    @Test
    @WithMockUser(roles = "USER")
    public void getPreguntasByCandidaturaShouldReturnPreguntas() throws Exception{
        // Datos de prueba
        User user = new User(UUID.randomUUID(), "testuser", "password", "test@example.com", Role.USER);

        Empresa empresa = new Empresa();
        empresa.setId(UUID.randomUUID());

        Candidatura candidatura = new Candidatura();
        candidatura.setId(UUID.randomUUID());
        candidatura.setUser(user);
        candidatura.setEmpresa(empresa);

        Pregunta pregunta1 = new Pregunta();
        pregunta1.setId(UUID.randomUUID());
        pregunta1.setCandidatura(candidatura);
        pregunta1.setPregunta("¿Cuál es tu experiencia en Java?");
        pregunta1.setUsuario(user);

        Pregunta pregunta2 = new Pregunta();
        pregunta2.setId(UUID.randomUUID());
        pregunta2.setCandidatura(candidatura);
        pregunta2.setPregunta("¿Has trabajado con Spring Boot?");
        pregunta2.setUsuario(user);

        List<Pregunta> preguntas = Arrays.asList(pregunta1, pregunta2);

        // Configurar comportamiento del mock
        when(preguntaService.findByCandidaturaId(candidatura.getId())).thenReturn(preguntas);

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(get("/api/preguntas")
                        .param("candidaturaId", candidatura.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pregunta").value("¿Cuál es tu experiencia en Java?"))
                .andExpect(jsonPath("$[1].pregunta").value("¿Has trabajado con Spring Boot?"));
    }

    /**
     * Verifica que se pueda obtener el conteo de preguntas por candidatura.
     */
    @Test
    @WithMockUser(roles = "USER")
    public void countPreguntasByCandidaturaShouldReturnCount() throws Exception{
        // Datos de prueba
        UUID candidaturaId = UUID.randomUUID();

        // Configurar comportamiento del mock
        when(preguntaService.countByCandidaturaId(candidaturaId)).thenReturn(5);

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(get("/api/preguntas/count")
                        .param("candidaturaId", candidaturaId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    /**
     * Verifica que un usuario pueda crear una pregunta.
     */
    @Test
    @WithMockUser(roles = "USER")
    public void createPreguntaShouldReturnCreatedPregunta() throws Exception{
        // Datos de prueba
        User currentUser = new User(UUID.randomUUID(), "testuser", "password", "test@example.com", Role.USER);

        Empresa empresa = new Empresa();
        empresa.setId(UUID.randomUUID());

        Candidatura candidatura = new Candidatura();
        candidatura.setId(UUID.randomUUID());
        candidatura.setUser(currentUser);
        candidatura.setEmpresa(empresa);


        Map<String, Object> preguntaRequest = new HashMap<>();
        preguntaRequest.put("candidaturaId", candidatura.getId().toString());
        preguntaRequest.put("pregunta", "¿Cuál es tu experiencia en Spring Boot?");

        Pregunta preguntaGuardada = new Pregunta();
        preguntaGuardada.setId(UUID.randomUUID());
        preguntaGuardada.setCandidatura(candidatura);
        preguntaGuardada.setPregunta("¿Cuál es tu experiencia en Spring Boot?");
        preguntaGuardada.setUsuario(currentUser);

        // Configurar comportamiento del mock
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(preguntaService.save(any(Pregunta.class))).thenReturn(preguntaGuardada);

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(post("/api/preguntas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(preguntaRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pregunta").value("¿Cuál es tu experiencia en Spring Boot?"));
    }

    
}
