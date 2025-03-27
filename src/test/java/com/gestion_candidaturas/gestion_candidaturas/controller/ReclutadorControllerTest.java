package com.gestion_candidaturas.gestion_candidaturas.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion_candidaturas.gestion_candidaturas.dto.PageResponseDTO;
import com.gestion_candidaturas.gestion_candidaturas.dto.ReclutadorDTO;
import com.gestion_candidaturas.gestion_candidaturas.model.Empresa;
import com.gestion_candidaturas.gestion_candidaturas.model.Reclutador;
import com.gestion_candidaturas.gestion_candidaturas.model.Role;
import com.gestion_candidaturas.gestion_candidaturas.model.User;
import com.gestion_candidaturas.gestion_candidaturas.security.JwtUtil;
import com.gestion_candidaturas.gestion_candidaturas.service.CandidaturaService;
import com.gestion_candidaturas.gestion_candidaturas.service.EmpresaService;
import com.gestion_candidaturas.gestion_candidaturas.service.ReclutadorService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Pruebas para el controlador de reclutadores.
 * Verifica operaciones CRUD y control de acceso y paginacion.
 */
@WebMvcTest(ReclutadorController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ReclutadorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReclutadorService reclutadorService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CandidaturaService candidaturaService;

    @MockitoBean
    private EmpresaService empresaService;

    @MockitoBean
    private JwtUtil jwtUtil;

    /**
     * Verifica que se puedan obtener todos los reclutadores con paginacion.
     */
    @Test
    @WithMockUser(roles = "USER")
    public void getAllReclutadoresShouldReturnReclutadores() throws Exception{
        // Datos de prueba
        Empresa empresa1 = new Empresa();
        empresa1.setId(UUID.randomUUID());
        empresa1.setNombre("Empresa Test 1");

        Empresa empresa2 = new Empresa();
        empresa2.setId(UUID.randomUUID());
        empresa2.setNombre("Empresa Test 2");

        Reclutador reclutador1 = new Reclutador();
        reclutador1.setId(UUID.randomUUID());
        reclutador1.setNombre("Reclutador 1");
        reclutador1.setEmpresa(empresa1);
        reclutador1.setLinkinUrl("https://linkedin.com/in/reclutador1");

        Reclutador reclutador2 = new Reclutador();
        reclutador2.setId(UUID.randomUUID());
        reclutador2.setNombre("Reclutador 2");
        reclutador2.setEmpresa(empresa2);
        reclutador2.setLinkinUrl("https://linkedin.com/in/reclutador2");

        List<Reclutador> reclutadores = Arrays.asList(reclutador1, reclutador2);

        // Crear objeto Page para respuesta paginada
        Page<Reclutador> reclutadoresPage = new PageImpl<>(reclutadores);

        // Configurar comportamiento del mock
        when(reclutadorService.findAll(any(Pageable.class))).thenReturn(reclutadoresPage);


        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(get("/api/reclutador")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "nombre, asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nombre").value("Reclutador 1"))
                .andExpect(jsonPath("$.content[1].nombre").value("Reclutador 2"))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.totalElements").value(reclutadores.size()));
    }

    /**
     * Verifica que se pueda obtener un reclutador por su ID.
     */
    @Test
    @WithMockUser(roles = "USER")
    public void getReclutadorByIdShouldReturnReclutador() throws Exception{
        // Datos de prueba
        UUID reclutadorId = UUID.randomUUID();

        Empresa empresa = new Empresa();
        empresa.setId(UUID.randomUUID());
        empresa.setNombre("Empresa Test");

        Reclutador reclutador = new Reclutador();
        reclutador.setId(reclutadorId);
        reclutador.setNombre("Reclutador Test");
        reclutador.setEmpresa(empresa);
        reclutador.setLinkinUrl("https://linkedin.com/in/reclutadortest");

        // Configurar comportamiento del mock
        when(reclutadorService.findById(reclutadorId)).thenReturn(Optional.of(reclutador));

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(get("/api/reclutador/{id}", reclutadorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Reclutador Test"))
                .andExpect(jsonPath("$.linkinUrl").value("https://linkedin.com/in/reclutadortest"));
    }

    /**
     * Verifica que se retorne un 404 al solicitar un reclutador inexistente.
     */
    @Test
    @WithMockUser(roles = "USER")
    public void getReclutadorByIdShouldReturn404WhenNotFound() throws Exception{
        // Datos de prueba
        UUID reclutadorId = UUID.randomUUID();

        // Configurar comportamiento del mock
        when(reclutadorService.findById(reclutadorId)).thenReturn(Optional.empty());

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(get("/api/reclutador/{id}", reclutadorId))
                .andExpect(status().isNotFound());
    }

    /**
     * Verifica que se puedan obtener los reclutadores por empresa con paginacion.
     */
    @Test
    @WithMockUser(roles = "USER")
    public void getReclutadoresByEmpresaShouldReturnReclutadores() throws Exception {
        // Datos de prueba
        UUID empresaId = UUID.randomUUID();

        Empresa empresa = new Empresa();
        empresa.setId(empresaId);
        empresa.setNombre("Empresa Test");

        Reclutador reclutador1 = new Reclutador();
        reclutador1.setId(UUID.randomUUID());
        reclutador1.setNombre("Reclutador 1");
        reclutador1.setEmpresa(empresa);
        reclutador1.setLinkinUrl("https://linkedin.com/in/reclutador1");

        Reclutador reclutador2 = new Reclutador();
        reclutador2.setId(UUID.randomUUID());
        reclutador2.setNombre("Reclutador 2");
        reclutador2.setEmpresa(empresa);
        reclutador2.setLinkinUrl("https://linkedin.com/in/reclutador2");

        List<Reclutador> reclutadores = Arrays.asList(reclutador1, reclutador2);

        // Crear objeto Page para respuesta paginada
        Page<Reclutador> reclutadoresPage = new PageImpl<>(reclutadores);

        // Crear pagina de DTOs para la respuesta esperada(mapeo que hace el controlador)
        Page<Map<String, Object>> reclutadoresDTOPage = reclutadoresPage.map(r -> {
            Map<String, Object> dto = new HashMap<>();
            dto.put("id", r.getId());
            dto.put("nombre", r.getNombre());
            dto.put("linkinUrl", r.getLinkinUrl());
            dto.put("empresaId", empresaId);
            return dto;
        });

        //PageResponseDTO que envolveria la respues de Page<Map<String, Object>>
        PageResponseDTO<Map<String, Object>> expectedResponse = new PageResponseDTO<>(reclutadoresDTOPage);

        // Configurar comportamiento del mock
        when(reclutadorService.findByEmpresaId(eq(empresaId), any(Pageable.class))).thenReturn(reclutadoresPage);

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(get("/api/reclutador/empresa/{empresaId}", empresaId)
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "nombre,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nombre").value("Reclutador 1"))
                .andExpect(jsonPath("$.content[1].nombre").value("Reclutador 2"))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.totalElements").value(reclutadores.size()));
    }

    /**
     * Verifica que un administrador pueda crear un reclutador.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    public void createReclutadorShouldReturnCreatedReclutador() throws Exception {
        // Datos de prueba
        UUID empresaId = UUID.randomUUID();

        Empresa empresa = new Empresa();
        empresa.setId(empresaId);
        empresa.setNombre("Empresa Test");

        ReclutadorDTO reclutadorDTO = new ReclutadorDTO();
        reclutadorDTO.setEmpresaId(empresaId);
        reclutadorDTO.setNombre("Nuevo Reclutador");
        reclutadorDTO.setLinkinUrl("https://linkedin.com/in/nuevoreclutador");

        Reclutador reclutadorGuardado = new Reclutador();
        reclutadorGuardado.setId(UUID.randomUUID());
        reclutadorGuardado.setNombre("Nuevo Reclutador");
        reclutadorGuardado.setEmpresa(empresa);
        reclutadorGuardado.setLinkinUrl("https://linkedin.com/in/nuevoreclutador");

        // Configurar comportamiento del mock
        when(empresaService.findById(empresaId)).thenReturn(Optional.of(empresa));
        when(reclutadorService.save(any(Reclutador.class))).thenReturn(reclutadorGuardado);

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(post("/api/reclutador")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reclutadorDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Nuevo Reclutador"));
    }

    /**
     * Verifica que un usuario pueda crear un reclutador durante la gestión de candidaturas.
     */
    @Test
    @WithMockUser(roles = "USER")
    public void createReclutadorForCandidaturaShouldReturnCreatedReclutador() throws Exception {
        // Datos de prueba
        UUID empresaId = UUID.randomUUID();

        Empresa empresa = new Empresa();
        empresa.setId(empresaId);
        empresa.setNombre("Empresa Test");

        // Crear DTO para la solicitud
        ReclutadorDTO reclutadorDTO = new ReclutadorDTO();
        reclutadorDTO.setNombre("Reclutador Candidatura");
        reclutadorDTO.setEmpresaId(empresaId);
        reclutadorDTO.setLinkinUrl("https://linkedin.com/in/reclutadorcandidatura");

        Reclutador reclutadorGuardado = new Reclutador();
        reclutadorGuardado.setId(UUID.randomUUID());
        reclutadorGuardado.setNombre("Reclutador Candidatura");
        reclutadorGuardado.setEmpresa(empresa);
        reclutadorGuardado.setLinkinUrl("https://linkedin.com/in/reclutadorcandidatura");

        // Configurar comportamiento del mock
        when(empresaService.findById(empresaId)).thenReturn(Optional.of(empresa));
        when(reclutadorService.findByNombreAndEmpresaId(reclutadorDTO.getNombre(), empresa.getId()))
                .thenReturn(Optional.empty());
        when(reclutadorService.save(any(Reclutador.class))).thenReturn(reclutadorGuardado);

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(post("/api/reclutador/crear-con-candidatura")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reclutadorDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Reclutador Candidatura"));
    }

    /**
     * Verifica que se devuelva un reclutador existente si ya existe al crearlo con candidatura.
     */
    @Test
    @WithMockUser(roles = "USER")
    public void createReclutadorForCandidaturaShouldReturnExistingWhenExists() throws Exception {
        // Datos de prueba
        UUID empresaId = UUID.randomUUID();

        Empresa empresa = new Empresa();
        empresa.setId(empresaId);
        empresa.setNombre("Empresa Test");

        // Crear DTO para la solicitud
        ReclutadorDTO reclutadorDTO = new ReclutadorDTO();
        reclutadorDTO.setNombre("Reclutador Existente");
        reclutadorDTO.setEmpresaId(empresaId);
        reclutadorDTO.setLinkinUrl("https://linkedin.com/in/reclutadorexistente");

        Reclutador reclutadorExistente = new Reclutador();
        reclutadorExistente.setId(UUID.randomUUID());
        reclutadorExistente.setNombre("Reclutador Existente");
        reclutadorExistente.setEmpresa(empresa);
        reclutadorExistente.setLinkinUrl("https://linkedin.com/in/reclutadorexistente");

        // Configurar comportamiento del mock
        when(empresaService.findById(empresaId)).thenReturn(Optional.of(empresa));
        when(reclutadorService.findByNombreAndEmpresaId(reclutadorDTO.getNombre(), empresa.getId()))
                .thenReturn(Optional.of(reclutadorExistente));

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(post("/api/reclutador/crear-con-candidatura")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reclutadorDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Reclutador Existente"));
    }

    /**
     * Verifica que un usuario pueda actualizar un reclutador asociado a sus candidaturas.
     */
    @Test
    @WithMockUser(roles = "USER")
    public void updateReclutadorByUserShouldReturnUpdatedReclutadorWhenAssociated() throws Exception {
        // Datos de prueba
        UUID reclutadorId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID empresaId = UUID.randomUUID();

        User currentUser = new User(userId, "testuser", "password", "test@example.com", Role.USER);

        Empresa empresa = new Empresa();
        empresa.setId(empresaId);
        empresa.setNombre("Empresa Test");

        Reclutador reclutadorExistente = new Reclutador();
        reclutadorExistente.setId(reclutadorId);
        reclutadorExistente.setNombre("Reclutador Asociado");
        reclutadorExistente.setEmpresa(empresa);
        reclutadorExistente.setLinkinUrl("https://linkedin.com/in/reclutadorasociado");

        ReclutadorDTO updateDTO = new ReclutadorDTO();
        updateDTO.setEmpresaId(empresaId);
        updateDTO.setNombre("Reclutador Actualizado");
        updateDTO.setLinkinUrl("https://linkedin.com/in/reclutadoractualizado");

        Reclutador reclutadorActualizado = new Reclutador();
        reclutadorActualizado.setId(reclutadorId);
        reclutadorActualizado.setNombre("Reclutador Actualizado");
        reclutadorActualizado.setEmpresa(empresa);
        reclutadorActualizado.setLinkinUrl("https://linkedin.com/in/reclutadoractualizado");

        // Configurar comportamiento del mock
        when(reclutadorService.findById(reclutadorId)).thenReturn(Optional.of(reclutadorExistente));
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(reclutadorService.isReclutadorAssociatedWithUserCandidaturas(reclutadorId, userId))
                .thenReturn(true);
        when(reclutadorService.save(any(Reclutador.class))).thenReturn(reclutadorActualizado);

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(put("/api/reclutador/{id}/user-update", reclutadorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Reclutador Actualizado"))
                .andExpect(jsonPath("$.linkinUrl").value("https://linkedin.com/in/reclutadoractualizado"));
    }

    /**
     * Verifica que un usuario no pueda actualizar un reclutador no asociado a sus candidaturas.
     */
    @Test
    @WithMockUser(roles = "USER")
    public void updateReclutadorByUserShouldReturnForbiddenWhenNotAssociated() throws Exception {
        // Datos de prueba
        UUID reclutadorId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID empresaId = UUID.randomUUID();

        User currentUser = new User(userId, "testuser", "password", "test@example.com", Role.USER);

        Empresa empresa = new Empresa();
        empresa.setId(empresaId);
        empresa.setNombre("Empresa Test");

        Reclutador reclutadorExistente = new Reclutador();
        reclutadorExistente.setId(reclutadorId);
        reclutadorExistente.setNombre("Reclutador No Asociado");
        reclutadorExistente.setEmpresa(empresa);
        reclutadorExistente.setLinkinUrl("https://linkedin.com/in/reclutadornoasociado");

        ReclutadorDTO updateDTO = new ReclutadorDTO();
        updateDTO.setEmpresaId(empresaId);
        updateDTO.setNombre("Reclutador Actualizado");
        updateDTO.setLinkinUrl("https://linkedin.com/in/reclutadoractualizado");

        // Configurar comportamiento del mock
        when(reclutadorService.findById(reclutadorId)).thenReturn(Optional.of(reclutadorExistente));
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(reclutadorService.isReclutadorAssociatedWithUserCandidaturas(reclutadorId, userId))
                .thenReturn(false);

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(put("/api/reclutador/{id}/user-update", reclutadorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifica que un administrador pueda actualizar cualquier reclutador.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    public void updateReclutadorShouldReturnUpdatedReclutadorWhenAdmin() throws Exception {
        // Datos de prueba
        UUID reclutadorId = UUID.randomUUID();
        UUID empresaId = UUID.randomUUID();

        Empresa empresa = new Empresa();
        empresa.setId(empresaId);
        empresa.setNombre("Empresa Test");

        Reclutador reclutadorExistente = new Reclutador();
        reclutadorExistente.setId(reclutadorId);
        reclutadorExistente.setNombre("Reclutador Admin");
        reclutadorExistente.setEmpresa(empresa);
        reclutadorExistente.setLinkinUrl("https://linkedin.com/in/reclutadoradmin");

        // Crear el DTO que se enviará en la solicitud
        ReclutadorDTO updateDTO = new ReclutadorDTO();
        updateDTO.setId(reclutadorId);
        updateDTO.setNombre("Reclutador Actualizado Por Admin");
        updateDTO.setEmpresaId(empresaId);
        updateDTO.setLinkinUrl("https://linkedin.com/in/reclutadoractualizadoadmin");

        // La entidad que esperamos que devuelva el servicio después de guardar
        Reclutador reclutadorActualizado = new Reclutador();
        reclutadorActualizado.setId(reclutadorId);
        reclutadorActualizado.setNombre("Reclutador Actualizado Por Admin");
        reclutadorActualizado.setEmpresa(empresa);
        reclutadorActualizado.setLinkinUrl("https://linkedin.com/in/reclutadoractualizadoadmin");

        // Configurar comportamiento del mock
        when(empresaService.findById(empresaId)).thenReturn(Optional.of(empresa));
        when(reclutadorService.findById(reclutadorId)).thenReturn(Optional.of(reclutadorExistente));
        when(reclutadorService.save(any(Reclutador.class))).thenReturn(reclutadorActualizado);

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(put("/api/reclutador/{id}", reclutadorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Reclutador Actualizado Por Admin"));
    }

    /**
     * Verifica que se retorne un 404 al intentar actualizar un reclutador inexistente.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    public void updateReclutadorShouldReturn404WhenNotFound() throws Exception {
        // Datos de prueba
        UUID reclutadorId = UUID.randomUUID();
        UUID empresaId = UUID.randomUUID();

        Empresa empresa = new Empresa();
        empresa.setId(empresaId);
        empresa.setNombre("Empresa Test");

        ReclutadorDTO updateDTO = new ReclutadorDTO();
        updateDTO.setId(reclutadorId);
        updateDTO.setNombre("Reclutador Inexistente");
        updateDTO.setEmpresaId(empresaId);
        updateDTO.setLinkinUrl("https://linkedin.com/in/reclutadorinexistente");

        // Configurar comportamiento del mock
        when(reclutadorService.findById(reclutadorId)).thenReturn(Optional.empty());
        when(empresaService.findById(empresaId)).thenReturn(Optional.of(empresa));

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(put("/api/reclutador/{id}", reclutadorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());
    }

    /**
     * Verifica que un administrador pueda eliminar un reclutador.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    public void deleteReclutadorShouldSucceedWhenAdmin() throws Exception {
        // Datos de prueba
        UUID reclutadorId = UUID.randomUUID();

        // Configurar comportamiento del mock
        when(reclutadorService.deleteById(reclutadorId)).thenReturn(true);

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(delete("/api/reclutador/{id}", reclutadorId))
                .andExpect(status().isNoContent());
    }

    /**
     * Verifica que se retorne un 404 al intentar eliminar un reclutador inexistente.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    public void deleteReclutadorShouldReturn404WhenNotFound() throws Exception {
        // Datos de prueba
        UUID reclutadorId = UUID.randomUUID();

        // Configurar comportamiento del mock
        when(reclutadorService.deleteById(reclutadorId)).thenReturn(false);

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(delete("/api/reclutador/{id}", reclutadorId))
                .andExpect(status().isNotFound());
    }

    /**
     * Verifica que se pueda asociar un reclutador a una candidatura.
     */
    @Test
    @WithMockUser(roles = "USER")
    public void asociarACandidaturaShouldSucceed() throws Exception {
        // Datos de prueba
        UUID reclutadorId = UUID.randomUUID();
        UUID candidaturaId = UUID.randomUUID();

        // Configurar comportamiento del mock
        when(reclutadorService.asociarACandidatura(reclutadorId, candidaturaId)).thenReturn(true);

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(post("/api/reclutador/{reclutadorId}/candidaturas/{candidaturaId}",
                        reclutadorId, candidaturaId))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que se retorne un 404 al intentar asociar un reclutador o candidatura inexistente.
     */
    @Test
    @WithMockUser(roles = "USER")
    public void asociarACandidaturaShouldReturn404WhenNotFound() throws Exception {
        // Datos de prueba
        UUID reclutadorId = UUID.randomUUID();
        UUID candidaturaId = UUID.randomUUID();

        // Configurar comportamiento del mock
        when(reclutadorService.asociarACandidatura(reclutadorId, candidaturaId)).thenReturn(false);

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(post("/api/reclutador/{reclutadorId}/candidaturas/{candidaturaId}",
                        reclutadorId, candidaturaId))
                .andExpect(status().isNotFound());
    }

    /**
     * Verifica que se pueda desasociar un reclutador de una candidatura.
     */
    @Test
    @WithMockUser(roles = "USER")
    public void desasociarDeCandidaturaShouldSucceed() throws Exception {
        // Datos de prueba
        UUID reclutadorId = UUID.randomUUID();
        UUID candidaturaId = UUID.randomUUID();

        // Configurar comportamiento del mock
        when(reclutadorService.desasociarDeCandidatura(reclutadorId, candidaturaId)).thenReturn(true);

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(delete("/api/reclutador/{reclutadorId}/candidaturas/{candidaturaId}",
                        reclutadorId, candidaturaId))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que se retorne un 404 al intentar desasociar un reclutador o candidatura inexistente.
     */
    @Test
    @WithMockUser(roles = "USER")
    public void desasociarDeCandidaturaShouldReturn404WhenNotFound() throws Exception {
        // Datos de prueba
        UUID reclutadorId = UUID.randomUUID();
        UUID candidaturaId = UUID.randomUUID();

        // Configurar comportamiento del mock
        when(reclutadorService.desasociarDeCandidatura(reclutadorId, candidaturaId)).thenReturn(false);

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(delete("/api/reclutador/{reclutadorId}/candidaturas/{candidaturaId}",
                        reclutadorId, candidaturaId))
                .andExpect(status().isNotFound());
    }

}
