package com.gestion_candidaturas.gestion_candidaturas.controller;

import com.gestion_candidaturas.gestion_candidaturas.dto.EmpresaWithUsersDTO;
import com.gestion_candidaturas.gestion_candidaturas.model.Empresa;
import com.gestion_candidaturas.gestion_candidaturas.service.EmpresaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Controlador REST para operaciones relacionadas con empresas.
 *
 * @see RF-08: Permitir agregar y gestionar información de empresas
 * @see RF-09: Permitir agregar teléfono principal de la empresa
 */
@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;


    /**
     * Constructor para inyección de dependencias.
     *
     * @param empresaService Servicio para operaciones con empresas
     */
    @Autowired
    public EmpresaController(EmpresaService empresaService){
        this.empresaService = empresaService;
    }

    /**
     * Obtiene todas las empresas según el rol del usuario.
     * Los usuarios normales ven las empresas de sus candidaturas, los administradores ven todas.
     *
     * @return Lista de empresas
     *
     * @see RF-08: Gestión de empresas
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ROOT')")
    public ResponseEntity<List<Empresa>> getAllEmpresas() {
        // Se obtienen todas las empresas - La lógica de filtrado por rol se maneja en el servicio
        List<Empresa> empresas = empresaService.findAll();
        return ResponseEntity.ok(empresas);
    }

    /**
     * Endpoint especializado para administradores que necesitan ver todas las empresas
     * junto con información de los usuarios que tienen candidaturas asociadas a ellas.
     * Proporciona una visión completa de qué usuarios están aplicando a qué empresas.
     *
     * @return Lista de empresas con información de usuarios asociados
     *
     * @see RF-08: Gestión de empresas (extensión para administradores)
     * @see RF-11: Control de acceso basado en roles (ADMIN y ROOT tienen acceso a información extendida)
     */
    @GetMapping("/with-users")
    @PreAuthorize("hasAnyRole('ADMIN', 'ROOT')")
    public ResponseEntity<List<EmpresaWithUsersDTO>> getAllEmpresasWithUsers() {
        // Obtenemos todas las empresas con información de usuarios asociados
        List<EmpresaWithUsersDTO> empresasWithUsers = empresaService.findAllWithAssociatedUsers();
        return ResponseEntity.ok(empresasWithUsers);
    }

    /**
     * Obtiene una empresa por su ID.
     *
     * @param id ID de la empresa
     * @return La empresa si existe, 404 si no
     *
     * @see RF-08: Consulta de información de empresas
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Empresa> getEmpresaById(@PathVariable UUID id) {
        Optional<Empresa> empresa = empresaService.findById(id);
        return empresa.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crea una nueva empresa (solo administradores).
     *
     * @param empresa Datos de la empresa a crear
     * @return La empresa creada
     *
     * @see RF-08: Creación de empresas por administradores
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Empresa> createEmpresa(@Valid @RequestBody Empresa empresa) {
        Empresa nuevaEmpresa = empresaService.save(empresa);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaEmpresa);
    }

    /**
     * Endpoint especializado para crear empresas durante el proceso de registro de candidaturas.
     * Este endpoint permite a usuarios normales crear empresas, pero solo en el contexto de añadir
     * una candidatura. Implementa una lógica de "buscar primero, crear si no existe" para evitar
     * duplicados en el sistema.
     *
     * @param empresa Datos de la empresa a crear o buscar
     * @return La empresa existente o la nueva empresa creada
     *
     * @see RF-08: Permitir a usuarios crear empresas durante el registro de candidaturas
     * @see CU-12: Caso de uso específico para crear empresas durante registro de candidaturas
     */
    @PostMapping("/crear-con-candidatura")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Empresa> createEmpresaForCandidatura(@Valid @RequestBody Empresa empresa) {
        // Verificar si ya existe la empresa por nombre para evitar duplicados
        Optional<Empresa> existingEmpresa = empresaService.findByNombre(empresa.getNombre());
        if (existingEmpresa.isPresent()) {
            // Si la empresa ya existe, la retornamos para ser usada en la candidatura
            return ResponseEntity.ok(existingEmpresa.get());
        }

        // Si no existe, creamos la nueva empresa
        Empresa nuevaEmpresa = empresaService.save(empresa);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaEmpresa);
    }

    /**
     * Actualiza una empresa existente.
     * Los usuarios normales solo pueden actualizar empresas que hayan creado.
     * Los administradores pueden actualizar cualquier empresa.
     *
     * @param id ID de la empresa a actualizar
     * @param empresa Datos actualizados
     * @return La empresa actualizada, 404 si no existe
     *
     * @see RF-08: Actualización de información de empresas
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ROOT')")
    public ResponseEntity<Empresa> updateEmpresa(@PathVariable UUID id,
                                                 @Valid @RequestBody Empresa empresa) {
        // Verificar si la empresa existe
        if (empresaService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Establecer el ID para asegurar que se actualiza la empresa correcta
        empresa.setId(id);

        // La lógica de permisos se maneja en el servicio o mediante configuración de seguridad
        Empresa empresaActualizada = empresaService.save(empresa);
        return ResponseEntity.ok(empresaActualizada);
    }

    /**
     * Elimina una empresa (solo administradores).
     *
     * @param id ID de la empresa a eliminar
     * @return 204 si se eliminó, 404 si no existe
     *
     * @see RF-08: Eliminación de empresas (solo administradores)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ROOT')")
    public ResponseEntity<Void> deleteEmpresa(@PathVariable UUID id){
        boolean eliminada = empresaService.deleteById(id);
        return  eliminada ? ResponseEntity.noContent().build() :
                ResponseEntity.notFound().build();
    }

    /**
     * Busca empresas que contengan el texto especificado en su nombre.
     *
     * @param nombre Texto a buscar en el nombre de la empresa
     * @return Lista de empresas coincidentes
     *
     * @see RF-08: Búsqueda de empresas
     */
    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ROOT')")
    public ResponseEntity<List<Empresa>> buscarPorNombre(@RequestParam String nombre) {
        List<Empresa> empresas = empresaService.findByNombreContaining(nombre);
        return ResponseEntity.ok(empresas);
    }
}
