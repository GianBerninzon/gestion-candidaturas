package com.gestion_candidaturas.gestion_candidaturas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion_candidaturas.gestion_candidaturas.dto.PreguntaDTO;
import com.gestion_candidaturas.gestion_candidaturas.model.*;
import com.gestion_candidaturas.gestion_candidaturas.security.JwtUtil;
import com.gestion_candidaturas.gestion_candidaturas.service.CandidaturaService;
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
    private CandidaturaService candidaturaService;

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

        UUID candiaturaId = UUID.randomUUID();

        Empresa empresa = new Empresa();
        empresa.setId(UUID.randomUUID());

        Candidatura candidatura = new Candidatura();
        candidatura.setId(candiaturaId);
        candidatura.setUser(currentUser);
        candidatura.setEmpresa(empresa);

        // Crear DTO para la solicitud
        PreguntaDTO preguntaDTO = new PreguntaDTO();
        preguntaDTO.setCandidaturaId(candiaturaId);
        preguntaDTO.setPregunta("¿Cuál es tu experiencia en Spring Boot?");


        Pregunta preguntaGuardada = new Pregunta();
        preguntaGuardada.setId(UUID.randomUUID());
        preguntaGuardada.setCandidatura(candidatura);
        preguntaGuardada.setPregunta("¿Cuál es tu experiencia en Spring Boot?");
        preguntaGuardada.setUsuario(currentUser);

        // Configurar comportamiento del mock
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(candidaturaService.findById(candiaturaId)).thenReturn(Optional.of(candidatura));
        when(preguntaService.save(any(Pregunta.class))).thenReturn(preguntaGuardada);

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(post("/api/preguntas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(preguntaDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pregunta").value("¿Cuál es tu experiencia en Spring Boot?"));
    }

    /**
     * Verifica que un usuario pueda actualizar una pregunta que le pertenece.
     */
    @Test
    @WithMockUser(roles = "USER")
    public void updatePreguntaShouldReturnUpdatedPreguntaWhenOwner() throws Exception{
        // Datos de prueba
        UUID preguntaId= UUID.randomUUID();
        User currentUser = new User(UUID.randomUUID(), "testuser", "password", "test@example.com", Role.USER);

        UUID candidaturaId = UUID.randomUUID();

        Empresa empresa = new Empresa();
        empresa.setId(UUID.randomUUID());


        Candidatura candidatura = new Candidatura();
        candidatura.setId(candidaturaId);
        candidatura.setUser(currentUser);
        candidatura.setEmpresa(empresa);

        // Se crea la pregunta existente (antes de la actualización)
        Pregunta preguntaExistente = new Pregunta();
        preguntaExistente.setId(preguntaId);
        preguntaExistente.setCandidatura(candidatura);
        preguntaExistente.setPregunta("¿Cuál es tu experiencia en Java?");
        preguntaExistente.setUsuario(currentUser);

        // Se crea DTo para la solicitud de actualización
        PreguntaDTO updateDTO = new PreguntaDTO();
        updateDTO.setCandidaturaId(candidaturaId);
        updateDTO.setPregunta("¿Cuántos años de experiencia tienes en Java?");

        // Se crea la pregunta que se espera después de la actualización
        Pregunta preguntaActualizada = new Pregunta();
        preguntaActualizada.setId(preguntaId);
        preguntaActualizada.setCandidatura(candidatura);
        preguntaActualizada.setPregunta("¿Cuántos años de experiencia tienes en Java?");
        preguntaActualizada.setUsuario(currentUser);

        // Configurar comportamiento del mock
        when(preguntaService.findById(preguntaId)).thenReturn(Optional.of(preguntaExistente));
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(candidaturaService.findById(candidaturaId)).thenReturn(Optional.of(candidatura));
        when(preguntaService.save(any(Pregunta.class))).thenReturn(preguntaActualizada);

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(put("/api/preguntas/{id}", preguntaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pregunta").value("¿Cuántos años de experiencia tienes en Java?"));
    }

    /**
     * Verifica que un usuario no pueda actualizar una pregunta que no le pertenece.
     */
    @Test
    @WithMockUser(roles = "USER")
    public void updatePreguntaShouldReturnForbiddenWhenNotOwner() throws Exception{
        // Datos de prueba
        UUID preguntaId = UUID.randomUUID();
        User currentUser = new User(UUID.randomUUID(), "testuser", "password", "test@example.com", Role.USER);
        User otherUser = new User(UUID.randomUUID(), "otheruser", "password", "other@example.com", Role.USER);

        UUID candidaturaId = UUID.randomUUID();

        Empresa empresa = new Empresa();
        empresa.setId(UUID.randomUUID());

        Candidatura candidatura = new Candidatura();
        candidatura.setId(candidaturaId);
        candidatura.setUser(otherUser);
        candidatura.setEmpresa(empresa);

        // Se crea la pregunta existente (Pertenece a otro usuario distinto al autenticado)
        Pregunta preguntaExistente = new Pregunta();
        preguntaExistente.setId(preguntaId);
        preguntaExistente.setCandidatura(candidatura);
        preguntaExistente.setPregunta("¿Cuál es tu experiencia en Java?");
        preguntaExistente.setUsuario(otherUser);

        // Crear DTO para la solicitud de actualización
        PreguntaDTO updateDTO = new PreguntaDTO();
        updateDTO.setCandidaturaId(candidaturaId);
        updateDTO.setPregunta("¿Cuántos años de experiencia tienes en Java?");

        // Configurar comportamiento del mock
        when(preguntaService.findById(preguntaId)).thenReturn(Optional.of(preguntaExistente));
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(candidaturaService.findById(candidaturaId)).thenReturn(Optional.of(candidatura));

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(put("/api/preguntas/{id}", preguntaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifica que un administrador pueda actualizar cualquier pregunta.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    public void updatePreguntaShouldSucceedWhenAdmin() throws Exception {
        // Datos de prueba
        UUID preguntaId = UUID.randomUUID();
        User adminUser = new User(UUID.randomUUID(), "admin", "password", "admin@example.com", Role.ADMIN);
        User preguntaOwner = new User(UUID.randomUUID(), "user", "password", "user@example.com", Role.USER);

        UUID candidaturaId = UUID.randomUUID();
        Empresa empresa = new Empresa();
        empresa.setId(UUID.randomUUID());

        Candidatura candidatura = new Candidatura();
        candidatura.setId(candidaturaId);
        candidatura.setUser(preguntaOwner);
        candidatura.setEmpresa(empresa);

        Pregunta preguntaExistente = new Pregunta();
        preguntaExistente.setId(preguntaId);
        preguntaExistente.setCandidatura(candidatura);
        preguntaExistente.setPregunta("¿Cuál es tu experiencia en Java?");
        preguntaExistente.setUsuario(preguntaOwner);

        PreguntaDTO updateDTO = new PreguntaDTO();
        updateDTO.setCandidaturaId(candidaturaId);
        updateDTO.setPregunta("¿Cuántos años de experiencia tienes en Java?");

        Pregunta preguntaActualizada = new Pregunta();
        preguntaActualizada.setId(preguntaId);
        preguntaActualizada.setCandidatura(candidatura);
        preguntaActualizada.setPregunta("¿Cuántos años de experiencia tienes en Java?");
        preguntaActualizada.setUsuario(preguntaOwner);

        // Configurar comportamiento del mock
        when(preguntaService.findById(preguntaId)).thenReturn(Optional.of(preguntaExistente));
        when(userService.getCurrentUser()).thenReturn(adminUser);
        when(candidaturaService.findById(candidaturaId)).thenReturn(Optional.of(candidatura));
        when(preguntaService.save(any(Pregunta.class))).thenReturn(preguntaActualizada);

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(put("/api/preguntas/{id}", preguntaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pregunta").value("¿Cuántos años de experiencia tienes en Java?"));
    }

    /**
     * Verifica que se retorne un 404 al intentar actualizar una pregunta que no existe.
     */
    @Test
    @WithMockUser(roles = "USER")
    public void updatePreguntaShouldReturn404WhenPreguntaNotFound() throws Exception {
        // Datos de prueba
        UUID preguntaId = UUID.randomUUID();

        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("candidaturaId", UUID.randomUUID().toString());
        updateRequest.put("pregunta", "¿Cuántos años de experiencia tienes en Java?");

        // Configurar comportamiento del mock
        when(preguntaService.findById(preguntaId)).thenReturn(Optional.empty());

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(put("/api/preguntas/{id}", preguntaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    /**
     * Verifica que un usuario pueda eliminar una pregunta que le pertenece.
     */
    @Test
    @WithMockUser(roles = "USER")
    public void deletePreguntaShouldSucceedWhenOwner() throws Exception {
        // Datos de prueba
        UUID preguntaId = UUID.randomUUID();
        User currentUser = new User(UUID.randomUUID(), "testuser", "password", "test@example.com", Role.USER);

        Empresa empresa = new Empresa();
        empresa.setId(UUID.randomUUID());

        Candidatura candidatura = new Candidatura();
        candidatura.setId(UUID.randomUUID());
        candidatura.setUser(currentUser);
        candidatura.setEmpresa(empresa);

        Pregunta pregunta = new Pregunta();
        pregunta.setId(preguntaId);
        pregunta.setCandidatura(candidatura);
        pregunta.setPregunta("¿Cuál es tu experiencia en Java?");
        pregunta.setUsuario(currentUser);

        // Configurar comportamiento del mock
        when(preguntaService.findById(preguntaId)).thenReturn(Optional.of(pregunta));
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(preguntaService.deleteById(preguntaId)).thenReturn(true);

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(delete("/api/preguntas/{id}", preguntaId))
                .andExpect(status().isNoContent());
    }

    /**
     * Verifica que un usuario no pueda eliminar una pregunta que no le pertenece.
     */
    @Test
    @WithMockUser(roles = "USER")
    public void deletePreguntaShouldReturnForbiddenWhenNotOwner() throws Exception {
        // Datos de prueba
        UUID preguntaId = UUID.randomUUID();
        User currentUser = new User(UUID.randomUUID(), "testuser", "password", "test@example.com", Role.USER);
        User otherUser = new User(UUID.randomUUID(), "otheruser", "password", "other@example.com", Role.USER);

        Empresa empresa = new Empresa();
        empresa.setId(UUID.randomUUID());

        Candidatura candidatura = new Candidatura();
        candidatura.setId(UUID.randomUUID());
        candidatura.setUser(otherUser);
        candidatura.setEmpresa(empresa);

        Pregunta pregunta = new Pregunta();
        pregunta.setId(preguntaId);
        pregunta.setCandidatura(candidatura);
        pregunta.setPregunta("¿Cuál es tu experiencia en Java?");
        pregunta.setUsuario(otherUser);

        // Configurar comportamiento del mock
        when(preguntaService.findById(preguntaId)).thenReturn(Optional.of(pregunta));
        when(userService.getCurrentUser()).thenReturn(currentUser);

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(delete("/api/preguntas/{id}", preguntaId))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifica que un administrador pueda eliminar cualquier pregunta.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    public void deletePreguntaShouldSucceedWhenAdmin() throws Exception {
        // Datos de prueba
        UUID preguntaId = UUID.randomUUID();
        User adminUser = new User(UUID.randomUUID(), "admin", "password", "admin@example.com", Role.ADMIN);
        User preguntaOwner = new User(UUID.randomUUID(), "user", "password", "user@example.com", Role.USER);

        Empresa empresa = new Empresa();
        empresa.setId(UUID.randomUUID());

        Candidatura candidatura = new Candidatura();
        candidatura.setId(UUID.randomUUID());
        candidatura.setUser(preguntaOwner);
        candidatura.setEmpresa(empresa);

        Pregunta pregunta = new Pregunta();
        pregunta.setId(preguntaId);
        pregunta.setCandidatura(candidatura);
        pregunta.setPregunta("¿Cuál es tu experiencia en Java?");
        pregunta.setUsuario(preguntaOwner);

        // Configurar comportamiento del mock
        when(preguntaService.findById(preguntaId)).thenReturn(Optional.of(pregunta));
        when(userService.getCurrentUser()).thenReturn(adminUser);
        when(preguntaService.deleteById(preguntaId)).thenReturn(true);

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(delete("/api/preguntas/{id}", preguntaId))
                .andExpect(status().isNoContent());
    }

    /**
     * Verifica que se retorne un 404 al intentar eliminar una pregunta que no existe.
     */
    @Test
    @WithMockUser(roles = "USER")
    public void deletePreguntaShouldReturn404WhenPreguntaNotFound() throws Exception {
        // Datos de prueba
        UUID preguntaId = UUID.randomUUID();

        // Configurar comportamiento del mock
        when(preguntaService.findById(preguntaId)).thenReturn(Optional.empty());

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(delete("/api/preguntas/{id}", preguntaId))
                .andExpect(status().isNotFound());
    }


}
