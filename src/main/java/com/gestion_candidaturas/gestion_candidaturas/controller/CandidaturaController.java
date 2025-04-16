package com.gestion_candidaturas.gestion_candidaturas.controller;

import com.gestion_candidaturas.gestion_candidaturas.dto.CandidaturaDTO;
import com.gestion_candidaturas.gestion_candidaturas.dto.CandidaturaWithEmpresaDTO;
import com.gestion_candidaturas.gestion_candidaturas.dto.PageResponseDTO;
import com.gestion_candidaturas.gestion_candidaturas.model.Candidatura;
import com.gestion_candidaturas.gestion_candidaturas.model.Empresa;
import com.gestion_candidaturas.gestion_candidaturas.model.EstadoCandidatura;
import com.gestion_candidaturas.gestion_candidaturas.model.Role;
import com.gestion_candidaturas.gestion_candidaturas.model.User;
import com.gestion_candidaturas.gestion_candidaturas.service.CandidaturaMapper;
import com.gestion_candidaturas.gestion_candidaturas.service.CandidaturaService;
import com.gestion_candidaturas.gestion_candidaturas.service.EmpresaService;
import com.gestion_candidaturas.gestion_candidaturas.service.UserService;
import com.gestion_candidaturas.gestion_candidaturas.util.PaginacionUtil;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
    private final CandidaturaMapper candidaturaMapper;

    /**
     * Constructor para inyección de dependencias.
     *
     * @param candidaturaService Servicio para operaciones con candidaturas
     * @param userService Servicio para operaciones con usuarios
     */
    public CandidaturaController(CandidaturaService candidaturaService, UserService userService
    , EmpresaService empresaService, CandidaturaMapper candidaturaMapper) {
        this.candidaturaService = candidaturaService;
        this.userService = userService;
        this.empresaService = empresaService;
        this.candidaturaMapper = candidaturaMapper;
    }

    /**
     * Obtiene las candidaturas del usuario autenticado con soporte para paginación y ordenamiento.
     *
     * @param page Número de página (0-indexed)
     * @param size Tamaño de la página
     * @param sort Campos y direcciones de ordenamiento
     * @return ResponseEntity con la página de candidaturas
     *
     * @see RF-03: Visualización de candidaturas con paginación
     */
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PageResponseDTO<CandidaturaWithEmpresaDTO>> getCandidaturas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha_desc") String[] sort){
        // Obtener el usuario actual
        User currentUser = userService.getCurrentUser();

        // Crear objeto Pageable con la información de paginación y ordenamiento
        Pageable pageable = PaginacionUtil.crearPageable(page, size, sort);

        // Obtener candidaturas paginadas del usuario
        Page<Candidatura> candidaturas = candidaturaService.findByUserId(currentUser.getId(), pageable);

        //convertir a DTO de respuesta paginada con empresas incluidas
        PageResponseDTO<CandidaturaWithEmpresaDTO> responseDTO = candidaturaMapper.toPageResponseDTO(candidaturas);
        
        return ResponseEntity.ok(responseDTO);
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
    public ResponseEntity<PageResponseDTO<CandidaturaWithEmpresaDTO>> getAllCandidaturas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha_desc") String[] sort) {
        Pageable pageable = PaginacionUtil.crearPageable(page, size, sort);
        Page<Candidatura> candidaturas = candidaturaService.findAll(pageable);

        // Convertir a DTO de respuesta paginada con empresas incluidas
        PageResponseDTO<CandidaturaWithEmpresaDTO> responseDTO = candidaturaMapper.toPageResponseDTO(candidaturas);
        return ResponseEntity.ok(responseDTO);
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
    public ResponseEntity<CandidaturaWithEmpresaDTO> getCandidaturaById(@PathVariable UUID id) {
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

            //Convierte a DTO de respuesta con empresa incluida
            CandidaturaWithEmpresaDTO responseDTO = candidaturaMapper.toResponseDTO(candidatura.get());
            return ResponseEntity.ok(responseDTO);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Crea una nueva candidatura asociada al usuario autenticado.
     *
     * @param candidaturaDTO Datos de la candidatura a crear
     * @return La candidatura creada
     *
     * @see RF-01: Registro de candidaturas
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CandidaturaWithEmpresaDTO> createCandidatura(@Valid @RequestBody CandidaturaDTO candidaturaDTO) {
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

        //Convertir a DTO de respuesta con empresa incluida
        CandidaturaWithEmpresaDTO responseDTO = candidaturaMapper.toResponseDTO(nuevaCandidatura);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    /**
     * Actualiza una candidatura existente.
     * Los usuarios normales solo pueden actualizar sus propias candidaturas.
     * Los administradores pueden actualizar cualquier candidatura.
     *
     * @param id ID de la candidatura a actualizar
     * @param candidaturaDTO Datos actualizados
     * @return La candidatura actualizada, 404 si no existe, 403 si no tiene permisos
     *
     * @see RF-01: Actualización de información de candidaturas
     * @see RF-11: Control de acceso basado en roles
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ROOT')")
    public ResponseEntity<CandidaturaWithEmpresaDTO> updateCandidatura(@PathVariable UUID id,
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

            // Convertir a DTO de respuesta con empresa incluida
            CandidaturaWithEmpresaDTO responseDTO = candidaturaMapper.toResponseDTO(candidaturaActualizada);
            return ResponseEntity.ok(responseDTO);
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
    public ResponseEntity<CandidaturaWithEmpresaDTO> updateEstado(@PathVariable UUID id,
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

            //Convertir a DTO de respuesta con empresa incluida
            CandidaturaWithEmpresaDTO responDto = candidaturaMapper.toResponseDTO(candidaturaActualizada);
            return ResponseEntity.ok(responDto);
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
//    @GetMapping("/buscar")
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity<List<Candidatura>> buscarCandidaturas(
//            @RequestParam(required = false) EstadoCandidatura estado,
//            @RequestParam(required = false) String empresa,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaDesde,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaHasta,
//            @RequestParam(required = false) String q) {
//
//        // Obtener el usuario actual
//        User currentUser = userService.getCurrentUser();
//
//        // Ejecutar búsqueda con filtros
//        List<Candidatura> candidaturas = candidaturaService.buscar(
//                estado, empresa, fechaDesde, fechaHasta, q, currentUser.getId());
//
//        return ResponseEntity.ok(candidaturas);
//    }

    /**
     * Busca candidaturas aplicando múltiples filtros opcionales con soporte para paginación.
     * Los usuarios normales solo ven sus propias candidaturas filtradas.
     *
     * @param estado Estado de la candidatura (opcional)
     * @param empresa Nombre de la empresa (opcional)
     * @param fechaDesde Fecha inicial para filtrar (opcional)
     * @param fechaHasta Fecha final para filtrar (opcional)
     * @param q Texto de búsqueda general en cargo o notas (opcional)
     * @param page Número de página (0-indexed)
     * @param size Tamaño de la página
     * @param sort Campos y direcciones de ordenamiento
     * @return Lista paginada de candidaturas que cumplen los criterios
     *
     * @see RF-03: Búsqueda y filtrado de candidaturas con paginación
     */
    @GetMapping("/buscar")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PageResponseDTO<CandidaturaWithEmpresaDTO>> buscarCandidaturas(
            @RequestParam(required = false) EstadoCandidatura estado,
            @RequestParam(required = false) String empresa,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaHasta,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha_desc") String[] sort){
        // Obtener el usuario actual
        User currentUser = userService.getCurrentUser();

        // Crear objeto Pageable
        Pageable pageable = PaginacionUtil.crearPageable(page, size, sort);

        // Ejecutar búsqueda con filtros
        Page<Candidatura> candidaturas = candidaturaService.buscar(
                estado, empresa, fechaDesde, fechaHasta, q, currentUser.getId(), pageable);

        // Convertir a DTO de respuesta paginada con empresas incluidas
        PageResponseDTO<CandidaturaWithEmpresaDTO> responseDTO = candidaturaMapper.toPageResponseDTO(candidaturas);

        // Convertir a DTO de respuesta paginada
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Permite a los usuarios administradores buscar candidaturas de todos los usuarios
     * con filtros opcionales por palabra clave y/o usuario específico.
     * Los usuarios normales serán redirigidos a ver solo sus propias candidaturas.
     *
     * @param q Texto de búsqueda para filtrar en cargo, notas o empresa (opcional)
     * @param usuario ID o username del usuario para filtrar (opcional, solo para ADMIN)
     * @param page Número de página (0-indexed)
     * @param size Tamaño de la página
     * @param sort Campos y direcciones de ordenamiento
     * @return Lista paginada de candidaturas que cumplen los criterios
     *
     * @see RF-03: Búsqueda y filtrado de candidaturas con control de acceso basado en roles
     * @see RF-11: Control de acceso basado en roles
     */
    @GetMapping("/filtrar")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ROOT')")
    public ResponseEntity<PageResponseDTO<CandidaturaWithEmpresaDTO>> filtrarCandidaturas(
        @RequestParam(required = false) String q,
        @RequestParam(required = false) String usuario,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "fecha_desc") String[] sort) {
        // Obtener el usuario actual
        User currUser = userService.getCurrentUser();

        // Crear objeto Pageable
        Pageable pageable = PaginacionUtil.crearPageable(page, size, sort);

        // Variable para almacenar las candidaturas segun el rol 
        Page<Candidatura> candidaturas;

        // Verificar si el usuario tiene rol ADMIN o ROOT
        boolean isAdmin = currUser.getRole() == Role.ADMIN || currUser.getRole() == Role.ROOT;

        if(isAdmin){
            // Si es admin puede ver todas las candidaturas o filtrar por usuario
            UUID userId= null;

            // Si se proporcionó un parámetro de usuario, intentamos resolverlo
            if(usuario != null && !usuario.isEmpty()){
                // Primero intentamos buscar por ID
                try{
                    userId = UUID.fromString(usuario);
                } catch (IllegalArgumentException e) {
                    // Si no es un UUID válido, intentamos buscar por username
                    Optional<User> targetUser = userService.findByUsername(usuario);
                    if(targetUser.isPresent()){
                        userId = targetUser.get().getId();
                    }
                }
            }

            // Ejecutar búsqueda con filtros
            candidaturas = candidaturaService.buscarAdmin(q, userId, pageable);
        }else {
            // Si es usuario normal, solo ve sus propias candidaturas
            // ignoramos el parametro usuario y filtramos solo por el usuario actual
            candidaturas = candidaturaService.buscarAdmin(q, currUser.getId(), pageable);
        }

        // Convertir a DTO de respuesta paginada con empresas incluidas
        PageResponseDTO<CandidaturaWithEmpresaDTO> responseDTO = candidaturaMapper.toPageResponseDTO(candidaturas);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Elimina multiples candidaturas por sus IDs (solo para ADMIN o ROOT).
     * 
     * @param ids Lista de IDs de candidaturas a eliminar
     * @return Respuesta con el numero de candidaturas eliminadas
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasAnyRole('ADMIN', 'ROOT')")
    public ResponseEntity<Map<String, Object>> deleteCandidaturasBatch(@RequestBody List<UUID> ids) {
        // Validar que la lista no este vacia
        if(ids == null || ids.isEmpty()){
            return ResponseEntity.badRequest().body(Map.of(
                "error", "La lista de IDs no puede estar vacia"
            ));
        }

        //Eliminar las candidaturas
        int eliminadas = candidaturaService.deleteAllByIds(ids);

        //Devolver respuesta con el conteno de eliminaciones
        Map<String, Object> response = new HashMap<>();
        response.put("eliminadas", eliminadas);
        response.put("total", ids.size());
        return ResponseEntity.ok(response);
    }
}

