package com.gestion_candidaturas.gestion_candidaturas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion_candidaturas.gestion_candidaturas.dto.EmpresaWithUsersDTO;
import com.gestion_candidaturas.gestion_candidaturas.model.Empresa;
import com.gestion_candidaturas.gestion_candidaturas.security.JwtUtil;
import com.gestion_candidaturas.gestion_candidaturas.service.EmpresaService;
import com.gestion_candidaturas.gestion_candidaturas.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas para el controlador de empresas.
 * Verifica las operaciones CRUD y acceso según roles.
 */
@WebMvcTest(EmpresaController.class)
// Desactiva filtros de seguridad para enfocarse en la lógica del controlador
@AutoConfigureMockMvc(addFilters = false)
public class EmpresaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EmpresaService empresaService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtUtil jwtUtil;

    /**
     * Verifica que se obtengan todas las empresas correctamente.
     */
    @Test
    @WithMockUser(roles = "USER")
    public void getAllEmpresasShouldReturnEmpresas() throws Exception{
        // Datos de prueba
        Empresa empresa1 = new Empresa();
        empresa1.setId(UUID.randomUUID());
        empresa1.setNombre("Empresa Test 1");
        empresa1.setCorreo("contacto@empresa1.com");

        Empresa empresa2 = new Empresa();
        empresa2.setId(UUID.randomUUID());
        empresa2.setNombre("Empresa Test 2");
        empresa2.setCorreo("contacto@empresa2.com");

        List<Empresa> empresas = Arrays.asList(empresa1, empresa2);

        // Configurar comportamiento del mock
        when(empresaService.findAll()).thenReturn(empresas);

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(get("/api/empresas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].nombre").value("Empresa Test 1"))
                .andExpect(jsonPath("$.[1].nombre").value("Empresa Test 2"));
    }

    /**
     * Verifica que se pueda obtener una empresa por su ID.
     */
    @Test
    @WithMockUser(roles = "USER")
    public void getEmpresaByIdShouldReturnEmpresa() throws Exception{
        // Datos de prueba
        UUID id = UUID.randomUUID();
        Empresa empresa = new Empresa();
        empresa.setId(id);
        empresa.setNombre("Empresa Test");
        empresa.setCorreo("contacto@empresa.com");

        // Configurar comportamiento del mock
        when(empresaService.findById(id)).thenReturn(Optional.of(empresa));

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(get("/api/empresas/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Empresa Test"))
                .andExpect(jsonPath("$.correo").value("contacto@empresa.com"));
    }

    /**
     * Verifica que se devuelva 404 si la empresa no existe.
     */
    @Test
    @WithMockUser(roles = "USER")
    public void getEmpresaByIdShouldReturn404WhenNotFound() throws Exception{
        // Configurar comportamiento del mock
        UUID id = UUID.randomUUID();

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(get("/api/empresas/{id}", id))
                .andExpect(status().isNotFound());
    }

    /**
     * Verifica que un admin pueda crear una empresa.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    public void createEmpresaShouldReturnCreatedWhenAdmin() throws Exception{
        // Datos de prueba
        Empresa empresa = new Empresa();
        empresa.setNombre("Nueva Empresa");
        empresa.setCorreo("contacto@nuevaempresa.com");

        Empresa empresaCreada = new Empresa();
        empresaCreada.setId(UUID.randomUUID());
        empresaCreada.setNombre("Nueva Empresa");
        empresaCreada.setCorreo("contacto@nuevaempresa.com");

        // Configurar comportamiento del mock
        when(empresaService.save(any(Empresa.class))).thenReturn(empresaCreada);

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(post("/api/empresas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(empresa)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Nueva Empresa"))
                .andExpect(jsonPath("$.correo").value("contacto@nuevaempresa.com"));
    }

    /**
     * Verifica que un usuario pueda crear una empresa durante el registro de candidatura.
     */
    @Test
    @WithMockUser(roles ="USER")
    public void createEmpresaForCandidaturaShouldReturnCreatedWhenNotExists() throws Exception{
        // Datos de prueba
        Empresa empresa = new Empresa();
        empresa.setNombre("Empresa Candidatura");
        empresa.setCorreo("contacto@nuevaempresa.com");

        Empresa empresaCreada = new Empresa();
        empresaCreada.setId(UUID.randomUUID());
        empresaCreada.setNombre("Empresa Candidatura");
        empresaCreada.setCorreo("contacto@nuevaempresa.com");

        // Configurar comportamiento del mock
        when(empresaService.findByNombre("Empresa Candidatura")).thenReturn(Optional.empty());
        when(empresaService.save(any(Empresa.class))).thenReturn(empresaCreada);

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(post("/api/empresas/crear-con-candidatura")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(empresa)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Empresa Candidatura"))
                .andExpect(jsonPath("$.correo").value("contacto@nuevaempresa.com"));
    }

    /**
     * Verifica que se retorne una empresa existente si ya existe al crear con candidatura.
     */
    @Test
    @WithMockUser(roles = "USER")
    public void createEmpresaForCandidaturaShouldReturnExistingWhenExists() throws Exception{
        // Datos de prueba
        Empresa empresa = new Empresa();
        empresa.setNombre("Empresa Existente");
        empresa.setCorreo("contacto@empresaexistente.com");

        Empresa empresaExistente = new Empresa();
        empresaExistente.setId(UUID.randomUUID());
        empresaExistente.setNombre("Empresa Existente");
        empresaExistente.setCorreo("contacto@empresaexistente.com");

        // Configurar comportamiento del mock
        when(empresaService.findByNombre("Empresa Existente")).thenReturn(Optional.of(empresaExistente));

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(post("/api/empresas/crear-con-candidatura")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(empresa)))
                .andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Empresa Existente"))
                .andExpect(jsonPath("$.correo").value("contacto@empresaexistente.com"));
    }

    /**
     * Verifica que un admin pueda ver empresas con información de usuarios.
     */
    @Test
    @WithMockUser(roles ="ADMIN")
    public void getAllEmpresasWithUsersShouldReturnDataWhenAdmin() throws Exception{
        // Datos de prueba
        List<EmpresaWithUsersDTO> empresaWithUsers = Arrays.asList(
                new EmpresaWithUsersDTO(UUID.randomUUID(), "Empresa 1", "correo1@empresa.com", "123456789", null),
                new EmpresaWithUsersDTO(UUID.randomUUID(), "Empresa 2", "correo2@empresa.com", "987654321", null)
        );

        // Configurar comportamiento del mock
        when(empresaService.findAllWithAssociatedUsers()).thenReturn(empresaWithUsers);

        // Ejecutar solicitud y verificar resultado
        mockMvc.perform(get("/api/empresas/with-users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Empresa 1"))
                .andExpect(jsonPath("$[1].nombre").value("Empresa 2"));
    }
}
