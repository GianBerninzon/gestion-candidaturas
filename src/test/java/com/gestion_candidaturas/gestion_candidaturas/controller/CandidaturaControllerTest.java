package com.gestion_candidaturas.gestion_candidaturas.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion_candidaturas.gestion_candidaturas.dto.CandidaturaDTO;
import com.gestion_candidaturas.gestion_candidaturas.model.*;
import com.gestion_candidaturas.gestion_candidaturas.security.JwtUtil;
import com.gestion_candidaturas.gestion_candidaturas.service.CandidaturaService;
import com.gestion_candidaturas.gestion_candidaturas.service.EmpresaService;
import com.gestion_candidaturas.gestion_candidaturas.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas para el controlador de candidaturas.
 * Verifica las operaciones CRUD y filtrado de candidaturas.
 * Incluye soporte para pruebas de paginacion.
 */
@WebMvcTest(CandidaturaController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CandidaturaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CandidaturaService candidaturaService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private EmpresaService empresaService;

    @MockitoBean
    private JwtUtil jwtUtil;


    /**
     * Verifica que un usuario pueda obtener sus propias candidaturas.
     */
    @Test
    @WithMockUser(roles = "USER")
    public void getCandidaturasShouldReturnUserCandidaturas() throws Exception{
        // Datos de prueba
        User currentUser = new User(UUID.randomUUID(), "testuser", "password", "test@example.com", Role.USER);

        Empresa empresa = new Empresa();
        empresa.setId(UUID.randomUUID());
        empresa.setNombre("Empresa Test");

        Candidatura candidatura1 = new Candidatura();
        candidatura1.setId(UUID.randomUUID());
        candidatura1.setUser(currentUser);
        candidatura1.setEmpresa(empresa);
        candidatura1.setCargo("Desarrollador Java");
        candidatura1.setEstado(EstadoCandidatura.PENDIENTE);

        Candidatura candidatura2 = new Candidatura();
        candidatura2.setId(UUID.randomUUID());
        candidatura2.setUser(currentUser);
        candidatura2.setEmpresa(empresa);
        candidatura2.setCargo("Analista de Datos");
        candidatura2.setEstado(EstadoCandidatura.ENTREVISTA);

        List<Candidatura> candidaturas = Arrays.asList(candidatura1, candidatura2);

        // Crear objeto Page para la respuesta paginada
        Page<Candidatura> candidaturasPage = new PageImpl<>(candidaturas);

        // Configurar comportamiento del mock
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(candidaturaService.findByUserId(eq(currentUser.getId()), any(Pageable.class))).thenReturn(candidaturasPage);

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(get("/api/candidaturas")
                    .param("page", "0")
                    .param("size", "10")
                    .param("sort", "fecha, desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].cargo").value("Desarrollador Java"))
                .andExpect(jsonPath("$.content[1].cargo").value("Analista de Datos"))
                .andExpect(jsonPath("$.pageSize").value(candidaturas.size()))
                .andExpect(jsonPath("$.totalElements").value(candidaturas.size()));
    }

    /**
     * Verifica que un administrador pueda obtener todas las candidaturas.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    public void getAllCandidaturasShouldReturnAllCandidaturasWhenAdmin() throws Exception{
        // Datos de prueba
        User user1 = new User(UUID.randomUUID(), "user1", "password", "user1@example.com", Role.USER);
        User user2 = new User(UUID.randomUUID(), "user2", "password", "user2@example.com", Role.USER);

        Empresa empresa = new Empresa();
        empresa.setId(UUID.randomUUID());
        empresa.setNombre("Empresa Test");

        Candidatura candidatura1 = new Candidatura();
        candidatura1.setId(UUID.randomUUID());
        candidatura1.setUser(user1);
        candidatura1.setEmpresa(empresa);
        candidatura1.setCargo("Desarrollador Java");

        Candidatura candidatura2 = new Candidatura();
        candidatura2.setId(UUID.randomUUID());
        candidatura2.setUser(user2);
        candidatura2.setEmpresa(empresa);
        candidatura2.setCargo("Analista de Datos");

        List<Candidatura> candidaturas = Arrays.asList(candidatura1, candidatura2);

        //Crear objeto Page para la respuesta Paginada
        Page<Candidatura> candidaturasPage = new PageImpl<>(candidaturas);

        // Configurar comportamiento del mock
        when(candidaturaService.findAll(any(Pageable.class))).thenReturn(candidaturasPage);

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(get("/api/candidaturas/all")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "fecha, desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].cargo").value("Desarrollador Java"))
                .andExpect(jsonPath("$.content[1].cargo").value("Analista de Datos"))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.totalElements").value(candidaturas.size()));
    }

    /**
     * Verifica que un usuario pueda obtener una candidatura por ID si le pertenece.
     */
    @Test
    @WithMockUser(roles = "USER")
    public void getCandidaturaByIdShouldReturnCandidaturaIfOwner() throws Exception{
        // Datos de prueba
        User currentUser = new User(UUID.randomUUID(), "testuser", "password", "test@example.com", Role.USER);

        Empresa empresa = new Empresa();
        empresa.setId(UUID.randomUUID());
        empresa.setNombre("Empresa Test");

        Candidatura candidatura = new Candidatura();
        candidatura.setId(UUID.randomUUID());
        candidatura.setUser(currentUser);
        candidatura.setEmpresa(empresa);
        candidatura.setCargo("Desarrollador Java");
        candidatura.setEstado(EstadoCandidatura.PENDIENTE);

        // Configurar comportamiento del mock
        when(candidaturaService.findById(candidatura.getId())).thenReturn(Optional.of(candidatura));
        when(userService.getCurrentUser()).thenReturn(currentUser);

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(get("/api/candidaturas/{id}", candidatura.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cargo").value("Desarrollador Java"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));

    }

    /**
     * Verifica que un usuario pueda crear una nueva candidatura.
     */
    @Test
    @WithMockUser(roles = "USER")
    public void createCandidaturaShouldReturnCreatedCandidatura() throws Exception{
        // Datos de prueba
        User currentUser = new User(UUID.randomUUID(), "testuser", "password", "test@example.com", Role.USER);

        UUID empresaId = UUID.randomUUID();
        Empresa empresa = new Empresa();
        empresa.setId(empresaId);
        empresa.setNombre("Empresa Test");

        // Crear DTO para enviar en la solicitud
        CandidaturaDTO candidaturaDTO = new CandidaturaDTO();
        candidaturaDTO.setEmpresaId(empresaId);
        candidaturaDTO.setCargo("Desarrollador Java");
        candidaturaDTO.setEstado(EstadoCandidatura.PENDIENTE);
        candidaturaDTO.setFecha(new Date());

        // Crear objeto Candidatura para la respuesta esperada
        Candidatura candidaturaGuardada = new Candidatura();
        candidaturaGuardada.setId(UUID.randomUUID());
        candidaturaGuardada.setUser(currentUser);
        candidaturaGuardada.setEmpresa(empresa);
        candidaturaGuardada.setCargo("Desarrollador Java");
        candidaturaGuardada.setEstado(EstadoCandidatura.PENDIENTE);
        candidaturaGuardada.setFecha(new Date());

        // Configurar comportamiento del mock
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(empresaService.findById(empresaId)).thenReturn(Optional.of(empresa));
        when(candidaturaService.save(any(Candidatura.class))).thenReturn(candidaturaGuardada);

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(post("/api/candidaturas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(candidaturaDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cargo").value("Desarrollador Java"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    /**
     * Verifica que un usuario pueda actualizar el estado de una candidatura.
     */
    @Test
    @WithMockUser(roles = "USER")
    public void updateEstadoShouldReturnUpdatedCandidatura() throws Exception{
        // Datos de prueba
        User currentUser = new User(UUID.randomUUID(), "testuser", "password", "test@example.com", Role.USER);

        Empresa empresa = new Empresa();
        empresa.setId(UUID.randomUUID());
        empresa.setNombre("Empresa Test");

        Candidatura candidatura = new Candidatura();
        candidatura.setId(UUID.randomUUID());
        candidatura.setUser(currentUser);
        candidatura.setEmpresa(empresa);
        candidatura.setCargo("Desarrollador Java");
        candidatura.setEstado(EstadoCandidatura.PENDIENTE);

        Candidatura candidaturaActualizada = new Candidatura();
        candidaturaActualizada.setId(UUID.randomUUID());
        candidaturaActualizada.setUser(currentUser);
        candidaturaActualizada.setEmpresa(empresa);
        candidaturaActualizada.setCargo("Desarrollador Java");
        candidaturaActualizada.setEstado(EstadoCandidatura.ENTREVISTA);

        // Configurar comportamiento del mock
        when(candidaturaService.findById(candidatura.getId())).thenReturn(Optional.of(candidatura));
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(candidaturaService.updateEstado(eq(candidatura.getId()), eq(EstadoCandidatura.ENTREVISTA)))
                .thenReturn(candidaturaActualizada);

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(patch("/api/candidaturas/{id}/estado", candidatura.getId())
                        .param("estado", "ENTREVISTA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ENTREVISTA"));
    }

    /**
     * Verifica que el filtrado de candidaturas funcione correctamente.
     */
    @Test
    @WithMockUser(roles = "USER")
    public void buscarCandidaturasShouldReturnFilteredCandidaturas() throws Exception{
        // Datos de prueba
        User currentUser = new User(UUID.randomUUID(), "testuser", "password", "test@example.com", Role.USER);

        Empresa empresa = new Empresa();
        empresa.setId(UUID.randomUUID());
        empresa.setNombre("Empresa Test");

        Candidatura candidatura1 = new Candidatura();
        candidatura1.setId(UUID.randomUUID());
        candidatura1.setUser(currentUser);
        candidatura1.setEmpresa(empresa);
        candidatura1.setCargo("Desarrollador Java");
        candidatura1.setEstado(EstadoCandidatura.ENTREVISTA);

        List<Candidatura> candidaturasFiltradas = List.of(candidatura1);

        // Crear objeto Page para respuesta paginada
        Page<Candidatura> candidaturasPage = new PageImpl<>(candidaturasFiltradas);

        // Configurar comportamiento del mock
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(candidaturaService.buscar(
                eq(EstadoCandidatura.ENTREVISTA),
                eq("Empresa"),
                any(),
                any(),
                eq("Java"),
                eq(currentUser.getId()),
                any(Pageable.class)
        )).thenReturn(candidaturasPage);

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(get("/api/candidaturas/buscar")
                        .param("estado", "ENTREVISTA")
                        .param("empresa", "Empresa")
                        .param("q", "Java")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "fecha, desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].cargo").value("Desarrollador Java"))
                .andExpect(jsonPath("$.content[0].estado").value("ENTREVISTA"))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.totalElements").value(candidaturasFiltradas.size()));

    }
}
