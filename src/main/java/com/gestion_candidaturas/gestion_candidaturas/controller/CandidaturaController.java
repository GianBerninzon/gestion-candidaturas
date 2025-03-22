package com.gestion_candidaturas.gestion_candidaturas.controller;

import com.gestion_candidaturas.gestion_candidaturas.dto.CandidaturaDTO;
import com.gestion_candidaturas.gestion_candidaturas.model.Candidatura;
import com.gestion_candidaturas.gestion_candidaturas.model.Empresa;
import com.gestion_candidaturas.gestion_candidaturas.model.EstadoCandidatura;
import com.gestion_candidaturas.gestion_candidaturas.model.User;
import com.gestion_candidaturas.gestion_candidaturas.service.CandidaturaService;
import com.gestion_candidaturas.gestion_candidaturas.service.EmpresaService;
import com.gestion_candidaturas.gestion_candidaturas.service.UserService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Controlador REST para operaciones relacionadas con candidaturas.
 *
 * @see RF-01: Permitir registrar candidaturas
 * @see RF-02: Permitir actualizar el estado de una candidatura
 * @see RF-03: Permitir buscar y filtrar candidaturas
 */
@RestController
@RequestMapping("/api/candidaturas")
public class CandidaturaController {


    private final CandidaturaService candidaturaService;
    private final UserService userService;
    private final EmpresaService empresaService;

    /**
     * Constructor para inyección de dependencias.
     *
     * @param candidaturaService Servicio para operaciones con candidaturas
     * @param userService Servicio para operaciones con usuarios
     */
    public CandidaturaController(CandidaturaService candidaturaService, UserService userService
    , EmpresaService empresaService) {
        this.candidaturaService = candidaturaService;
        this.userService = userService;
        this.empresaService = empresaService;
    }

    /**
     * Obtiene todas las candidaturas del usuario autenticado.
     *
     * @return Lista de candidaturas del usuario
     *
     * @see RF-03: Visualización de candidaturas
     */
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Candidatura>> getCandidaturas() {
        // Obtener el usuario actual
        User currentUser = userService.getCurrentUser();

        // Obtener candidaturas del usuario
        List<Candidatura> candidaturas = candidaturaService.findByUserId(currentUser.getId());
        return ResponseEntity.ok(candidaturas);
    }

    /**
     * Obtiene todas las candidaturas del sistema (solo administradores).
     *
     * @return Lista completa de candidaturas
     *
     * @see RF-03: Visualización de candidaturas (administradores)
     * @see RF-11: Control de acceso basado en roles
     */
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'ROOT')")
    public ResponseEntity<List<Candidatura>> getAllCandidaturas() {
        List<Candidatura> candidaturas = candidaturaService.findAll();
        return ResponseEntity.ok(candidaturas);
    }

    /**
     * Obtiene una candidatura específica por su ID.
     * Los usuarios normales solo pueden ver sus propias candidaturas.
     * Los administradores pueden ver cualquier candidatura.
     *
     * @param id ID de la candidatura
     * @return La candidatura si existe y el usuario tiene acceso, error 404 o 403 en caso contrario
     *
     * @see RF-03: Consulta detallada de candidaturas
     * @see RF-11: Control de acceso basado en roles
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ROOT')")
    public ResponseEntity<Candidatura> getCandidaturaById(@PathVariable UUID id) {
        // Buscar la candidatura
        Optional<Candidatura> candidatura = candidaturaService.findById(id);

        if (candidatura.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Obtener el usuario actual
        User currentUser = userService.getCurrentUser();

        // Verificar permisos de acceso
        if (candidatura.get().getUser().getId().equals(currentUser.getId()) ||
                currentUser.hasRole("ADMIN") || currentUser.hasRole("ROOT")) {
            return ResponseEntity.ok(candidatura.get());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Crea una nueva candidatura asociada al usuario autenticado.
     *
     * @param candidatura Datos de la candidatura a crear
     * @return La candidatura creada
     *
     * @see RF-01: Registro de candidaturas
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Candidatura> createCandidatura(@Valid @RequestBody CandidaturaDTO candidaturaDTO) {
        // Obtener el usuario actual
        User currentUser = userService.getCurrentUser();

        // Buscar la empresa por ID
        Optional<Empresa> empresaOpt = empresaService.findById(candidaturaDTO.getEmpresaId());
        if(empresaOpt.isEmpty()){
            return ResponseEntity.badRequest().build();
        }

        // Crear la nueva candidatura
        Candidatura candidatura = new Candidatura();
        candidatura.setUser(currentUser);
        candidatura.setEmpresa(empresaOpt.get());
        candidatura.setCargo(candidaturaDTO.getCargo());
        candidatura.setFecha(candidaturaDTO.getFecha());
        candidatura.setEstado(candidaturaDTO.getEstado());
        candidatura.setNotas(candidaturaDTO.getNotas());

        // Guardar la candidatura
        Candidatura nuevaCandidatura = candidaturaService.save(candidatura);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCandidatura);
    }

    /**
     * Actualiza una candidatura existente.
     * Los usuarios normales solo pueden actualizar sus propias candidaturas.
     * Los administradores pueden actualizar cualquier candidatura.
     *
     * @param id ID de la candidatura a actualizar
     * @param candidatura Datos actualizados
     * @return La candidatura actualizada, 404 si no existe, 403 si no tiene permisos
     *
     * @see RF-01: Actualización de información de candidaturas
     * @see RF-11: Control de acceso basado en roles
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ROOT')")
    public ResponseEntity<Candidatura> updateCandidatura(@PathVariable UUID id,
                                                         @Valid @RequestBody CandidaturaDTO candidaturaDTO) {
        // Buscar la candidatura existente
        Optional<Candidatura> candidaturaExistente = candidaturaService.findById(id);

        if (candidaturaExistente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Obtener el usuario actual
        User currentUser = userService.getCurrentUser();

        // Verificar permisos de modificación
        if (candidaturaExistente.get().getUser().getId().equals(currentUser.getId()) ||
                currentUser.hasRole("ADMIN") || currentUser.hasRole("ROOT")) {

            // Buscar la empresa por ID
            Optional<Empresa> empresaOpt = empresaService.findById(candidaturaDTO.getEmpresaId());
            if(empresaOpt.isEmpty()){
                return ResponseEntity.badRequest().build();
            }

            // Actualizar la candidatura existente con los datos del DTO
            Candidatura candidatura = candidaturaExistente.get();
            candidatura.setEmpresa(empresaOpt.get());
            candidatura.setCargo(candidaturaDTO.getCargo());
            candidatura.setFecha(candidaturaDTO.getFecha());
            candidatura.setEstado(candidaturaDTO.getEstado());
            candidatura.setNotas(candidaturaDTO.getNotas());

            // Guardar cambios
            Candidatura candidaturaActualizada = candidaturaService.save(candidatura);
            return ResponseEntity.ok(candidaturaActualizada);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Actualiza solo el estado de una candidatura existente.
     * Es una operación parcial (PATCH) que modifica únicamente el campo estado.
     *
     * @param id ID de la candidatura
     * @param estado Nuevo estado a asignar
     * @return La candidatura con el estado actualizado, 404 si no existe, 403 si no tiene permisos
     *
     * @see RF-02: Actualización del estado de candidaturas
     * @see RF-11: Control de acceso basado en roles
     */
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ROOT')")
    public ResponseEntity<Candidatura> updateEstado(@PathVariable UUID id,
                                                    @RequestParam EstadoCandidatura estado) {
        // Buscar la candidatura existente
        Optional<Candidatura> candidaturaExistente = candidaturaService.findById(id);

        if (candidaturaExistente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Obtener el usuario actual
        User currentUser = userService.getCurrentUser();

        // Verificar permisos de modificación
        if (candidaturaExistente.get().getUser().getId().equals(currentUser.getId()) ||
                currentUser.hasRole("ADMIN") || currentUser.hasRole("ROOT")) {

            // Actualizar solo el estado
            Candidatura candidaturaActualizada = candidaturaService.updateEstado(id, estado);
            return ResponseEntity.ok(candidaturaActualizada);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Elimina una candidatura (solo administradores).
     *
     * @param id ID de la candidatura a eliminar
     * @return 204 si se eliminó correctamente, 404 si no existe
     *
     * @see RF-01: Eliminación de candidaturas (solo administradores)
     * @see RF-11: Control de acceso basado en roles
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ROOT')")
    public ResponseEntity<Void> deleteCandidatura(@PathVariable UUID id) {
        boolean eliminada = candidaturaService.deleteById(id);
        return eliminada ? ResponseEntity.noContent().build() :
                ResponseEntity.notFound().build();
    }

    /**
     * Busca candidaturas aplicando múltiples filtros opcionales.
     * Los usuarios normales solo ven sus propias candidaturas filtradas.
     *
     * @param estado Estado de la candidatura (opcional)
     * @param empresa Nombre de la empresa (opcional)
     * @param fechaDesde Fecha inicial para filtrar (opcional)
     * @param fechaHasta Fecha final para filtrar (opcional)
     * @param q Texto de búsqueda general en cargo o notas (opcional)
     * @return Lista de candidaturas que cumplen los criterios
     *
     * @see RF-03: Búsqueda y filtrado de candidaturas
     */
    @GetMapping("/buscar")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Candidatura>> buscarCandidaturas(
            @RequestParam(required = false) EstadoCandidatura estado,
            @RequestParam(required = false) String empresa,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaHasta,
            @RequestParam(required = false) String q) {

        // Obtener el usuario actual
        User currentUser = userService.getCurrentUser();

        // Ejecutar búsqueda con filtros
        List<Candidatura> candidaturas = candidaturaService.buscar(
                estado, empresa, fechaDesde, fechaHasta, q, currentUser.getId());

        return ResponseEntity.ok(candidaturas);
    }
}
