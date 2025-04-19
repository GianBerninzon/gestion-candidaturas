package com.gestion_candidaturas.gestion_candidaturas.controller;

import com.gestion_candidaturas.gestion_candidaturas.dto.CandidaturaDTO;
import com.gestion_candidaturas.gestion_candidaturas.dto.EmpresaDTO;
import com.gestion_candidaturas.gestion_candidaturas.dto.EmpresaWithCandidaturasDTO;
import com.gestion_candidaturas.gestion_candidaturas.dto.EmpresaWithUsersDTO;
import com.gestion_candidaturas.gestion_candidaturas.dto.PageResponseDTO;
import com.gestion_candidaturas.gestion_candidaturas.model.Candidatura;
import com.gestion_candidaturas.gestion_candidaturas.model.Empresa;
import com.gestion_candidaturas.gestion_candidaturas.model.Role;
import com.gestion_candidaturas.gestion_candidaturas.model.User;
import com.gestion_candidaturas.gestion_candidaturas.service.CandidaturaMapper;
import com.gestion_candidaturas.gestion_candidaturas.service.CandidaturaService;
import com.gestion_candidaturas.gestion_candidaturas.service.EmpresaService;
import com.gestion_candidaturas.gestion_candidaturas.service.UserService;
import com.gestion_candidaturas.gestion_candidaturas.util.PaginacionUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controlador REST para operaciones relacionadas con empresas.
 *
 * @see RF-08: Permitir agregar y gestionar información de empresas
 * @see RF-09: Permitir agregar teléfono principal de la empresa
 */
@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    private final AuthController authController;

    private final EmpresaService empresaService;
    private final UserService userService;
    private final CandidaturaService candidaturaService;
    private final CandidaturaMapper candidaturaMapper;


    /**
     * Constructor para inyección de dependencias.
     *
     * @param empresaService Servicio para operaciones con empresas
     * @param userService Servicio para operaciones con usuarios
     * @param candidaturaService Servicio para operaciones con candidaturas
     * @param candidaturaMapper Mapper para convertir entre entidades y DTOs
     */
    @Autowired
    public EmpresaController(EmpresaService empresaService, UserService userService, CandidaturaService candidaturaService, CandidaturaMapper candidaturaMapper, AuthController authController){
        this.empresaService = empresaService;
        this.userService = userService;
        this.candidaturaService = candidaturaService;
        this.candidaturaMapper = candidaturaMapper;
        this.authController = authController;
    }

    /**
     * Obtiene todas las empresas según el rol del usuario.
     * Los usuarios normales ven las empresas de sus candidaturas, los administradores ven todas.
     *
     * @return Lista de empresas
     *
     * @see RF-08: Gestión de empresas
     */
//    @GetMapping
//    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ROOT')")
//    public ResponseEntity<List<Empresa>> getAllEmpresas() {
//        // Se obtienen todas las empresas - La lógica de filtrado por rol se maneja en el servicio
//        List<Empresa> empresas = empresaService.findAll();
//        return ResponseEntity.ok(empresas);
//    }

    /**
     * Obtiene todas las empresas según el rol del usuario con soporte para paginación.
     * Los usuarios normales ven las empresas de sus candidaturas, los administradores ven todas.
     *
     * @param page Número de página (0-indexed)
     * @param size Tamaño de la página
     * @param sort Campos y direcciones de ordenamiento
     * @return ResponseEntity con la página de empresas
     *
     * @see RF-08: Gestión de empresas con paginación
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ROOT')")
    public ResponseEntity<PageResponseDTO<Map<String, Object>>> getAllEmpresas(
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "10")int size,
            @RequestParam(defaultValue = "nombre, asc") String[] sort){
        // Obtener el usuario actual
        User currentUser = userService.getCurrentUser();

        // Crear objeto Pageable con la informacion de paginacion y ordenamiento
        Pageable pageable = PaginacionUtil.crearPageable(page, size, sort);

        // Se obtienen todas las empresa paginadas
        Page<Empresa> empresasPage = empresaService.findAll(pageable);

        //Convertir a un Page con Maps que incluyan la informacion adicional
        Page<Map<String, Object>> erichedPage = empresasPage.map(empresa -> {
            Map<String, Object> empresaMap = new HashMap<>();
            empresaMap.put("id", empresa.getId());
            empresaMap.put("nombre", empresa.getNombre());
            empresaMap.put("correo", empresa.getCorreo());
            empresaMap.put("telefono", empresa.getTelefono());
            
            // Agregar info de si el usuario tiene candidaturas en esta empresa
            if(currentUser.hasRole("ADMIN") || currentUser.hasRole("ROOT")){
                empresaMap.put("userHasCandidatura", true);
            }else{
                boolean hasCandidatura = candidaturaService.existsByUserIdAndEmpresaId(
                    currentUser.getId(), empresa.getId());
                empresaMap.put("userHasCandidatura", hasCandidatura);
            }
            return empresaMap;
        });

        // Crear y devolver la respuesta paginada
        return ResponseEntity.ok(new PageResponseDTO<>(erichedPage));
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
     * Los usuarios normales solo pueden ver empresas de sus candidaturas.
     * Los administradores pueden ver cualquier empresa.
     *
     * @param id ID de la empresa
     * @return La empresa si existe y el usuario tiene acceso, error 404 o 403 en caso contrario
     *
     * @see RF-08: Consulta de información de empresas
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ROOT')")
    public ResponseEntity<?> getEmpresaById(
        @PathVariable UUID id,
        @RequestParam(required = false, defaultValue = "false") boolean includeCandidaturas,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "fecha, asc") String[] sort
        ) {

        Optional<Empresa> empresa = empresaService.findById(id);
        if(empresa.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        // Obtener el usuario actual
        User currentUser = userService.getCurrentUser();
        System.out.println("Roles de usuario:" + currentUser.getRole());

        // Verificar permisos: ADMIN/ROOT pueden ver cualquier empresa
        // USER solo puede ver empresas asociadas a sus candidaturas
        if(currentUser.getRole() == Role.USER){
            // Verificar si el usuario tiene candidaturas en esta empresa
            boolean tieneCandidatura = candidaturaService.existsByUserIdAndEmpresaId(currentUser.getId(), id);
            if(!tieneCandidatura){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        // Si se solicitan candidaturas y el usuario es admin o root, incluir candidaturas
        if(includeCandidaturas && (currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.ROOT)){
            // Crear objeto pageable con la informacion de paginacion y ordenamiento
            Pageable pageable = PaginacionUtil.crearPageable(page, size, sort);
            
            // Incluir candidaturas en el response
            EmpresaWithCandidaturasDTO empresaDTO = new EmpresaWithCandidaturasDTO();
            Empresa emp = empresa.get();
            empresaDTO.setId(emp.getId());
            empresaDTO.setNombre(emp.getNombre());
            empresaDTO.setCorreo(emp.getCorreo());
            empresaDTO.setTelefono(emp.getTelefono());

            //Obtener candidaturas de esta empresa paginadas
            Page<Candidatura> candidaturasPage = candidaturaService.findByEmpresaId(id, pageable);

            //Obtener candidaturas de esta empresa
            List<CandidaturaDTO> candidaturasDTO = candidaturasPage.getContent().stream()
                .map(candidaturaMapper::toDTO)
                .collect(Collectors.toList());

            empresaDTO.setCandidaturas(candidaturasDTO);
            
            return ResponseEntity.ok(empresaDTO);
        }

        // Caso normal: devolver solo la empresa
        return ResponseEntity.ok(empresa.get());
    }

    /**
     * Crea una nueva empresa (solo administradores).
     *
     * @param empresaDTO Datos de la empresa a crear
     * @return La empresa creada
     *
     * @see RF-08: Creación de empresas por administradores
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Empresa> createEmpresa(@Valid @RequestBody EmpresaDTO empresaDTO) {
        // Crear una nueva empresa a partir del DTO
        Empresa empresa = new Empresa();
        empresa.setNombre(empresaDTO.getNombre());
        empresa.setCorreo(empresaDTO.getCorreo());
        empresa.setTelefono(empresaDTO.getTelefono());

        // Guardar la empresa
        Empresa nuevaEmpresa = empresaService.save(empresa);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaEmpresa);
    }

    /**
     * Endpoint especializado para crear empresas durante el proceso de registro de candidaturas.
     * Este endpoint permite a usuarios normales crear empresas, pero solo en el contexto de añadir
     * una candidatura. Implementa una lógica de "buscar primero, crear si no existe" para evitar
     * duplicados en el sistema.
     *
     * @param empresaDTO Datos de la empresa a crear o buscar
     * @return La empresa existente o la nueva empresa creada
     *
     * @see RF-08: Permitir a usuarios crear empresas durante el registro de candidaturas
     * @see CU-12: Caso de uso específico para crear empresas durante registro de candidaturas
     */
    @PostMapping("/crear-con-candidatura")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Empresa> createEmpresaForCandidatura(@Valid @RequestBody EmpresaDTO empresaDTO) {
        // Verificar si ya existe la empresa por nombre para evitar duplicados
        Optional<Empresa> existingEmpresa = empresaService.findByNombre(empresaDTO.getNombre());
        if (existingEmpresa.isPresent()) {
            // Si la empresa ya existe, la retornamos para ser usada en la candidatura
            return ResponseEntity.ok(existingEmpresa.get());
        }

        // Crear una nueva empresa a partir del DTO
        Empresa empresa = new Empresa();
        empresa.setNombre(empresaDTO.getNombre());
        empresa.setCorreo(empresaDTO.getCorreo());
        empresa.setTelefono(empresaDTO.getTelefono());

        // Guardar la empresa
        Empresa nuevaEmpresa = empresaService.save(empresa);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaEmpresa);
    }

    /**
     * Actualiza una empresa existente.
     * Los usuarios normales solo pueden actualizar empresas que hayan creado.
     * Los administradores pueden actualizar cualquier empresa.
     *
     * @param id ID de la empresa a actualizar
     * @param empresaDTO Datos actualizados
     * @return La empresa actualizada, 404 si no existe, 403 si no tiene permiso
     *
     * @see RF-08: Actualización de información de empresas
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ROOT')")
    public ResponseEntity<Empresa> updateEmpresa(@PathVariable UUID id,
                                                 @Valid @RequestBody EmpresaDTO empresaDTO) {
        // Verificar si la empresa existe
        Optional<Empresa> empresaExistente = empresaService.findById(id);
        if (empresaExistente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Obtener el usuario actual
        User currentUser = userService.getCurrentUser();

        //Verificar permisos: ADMIN/ROOT pueden editar cualquier empresa
        // USER solo puede editar empresas asociadas a sus candidaturas
        if(currentUser.hasRole("USER") && !currentUser.hasRole("ADMIN") && !currentUser.hasRole("ROOT")){
            //Verificar si el usuario tiene candidaturas en esta empresa
            boolean tieneCandidatura = candidaturaService.existsByUserIdAndEmpresaId(currentUser.getId(), id);
            if(!tieneCandidatura){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        
        // Actualizar la empresa con los datos del DTO
        Empresa empresa = empresaExistente.get();
        empresa.setNombre(empresaDTO.getNombre());
        empresa.setCorreo(empresaDTO.getCorreo());
        empresa.setTelefono(empresaDTO.getTelefono());

        // Establecer el ID para asegurar que se actualiza la empresa correcta
        empresa.setId(id);

        // Guardar los cambios
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
     * Elimina multiples empresas por sus IDs (solo administradores).
     * 
     * @param ids Lista de IDs de empresas a eliminar
     * @return ResponseEntity con el número de empresas eliminadas.
     * 
     * @see RF-08: Eliminación de empresas (solo administradores)
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasAnyRole('ADMIN', 'ROOT')")
    public ResponseEntity<Map<String, Object>> deleteEmpresasBatch(@RequestBody List<UUID> ids) {
        // Validar que la lista no este vacia
        if(ids == null || ids.isEmpty()){
            return ResponseEntity.badRequest().body(Map.of(
                "error", "La lista de IDs no puede estar vacía"
            ));
        }

        //Eliminar las empresas
        int eliminadas = empresaService.deleteAllByIds(ids);

        //Devolver respuesta con el conteo de eliminaciones
        Map<String, Object> response = new HashMap<>();
        response.put("eliminadas", eliminadas);
        response.put("total", ids.size());

        return ResponseEntity.ok(response); 
    }

//    /**
//     * Busca empresas que contengan el texto especificado en su nombre.
//     *
//     * @param nombre Texto a buscar en el nombre de la empresa
//     * @return Lista de empresas coincidentes
//     *
//     * @see RF-08: Búsqueda de empresas
//     */
//    @GetMapping("/buscar")
//    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ROOT')")
//    public ResponseEntity<List<Empresa>> buscarPorNombre(@RequestParam String nombre) {
//        List<Empresa> empresas = empresaService.findByNombreContaining(nombre);
//        return ResponseEntity.ok(empresas);
//    }
    /**
     * Busca empresas que contengan el texto especificado en su nombre con soporte para paginación.
     *
     * @param nombre Texto a buscar en el nombre de la empresa
     * @param page Número de página (0-indexed)
     * @param size Tamaño de la página
     * @param sort Campos y direcciones de ordenamiento
     * @return ResponseEntity con la página de empresas coincidentes
     *
     * @see RF-08: Búsqueda de empresas con paginación
     */
    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ROOT')")
    public ResponseEntity<PageResponseDTO<Empresa>> buscarPorNombre(
            @RequestParam String nombre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombre,asc") String[] sort){
        // Crear objeto Paheable
        Pageable pageable = PaginacionUtil.crearPageable(page, size, sort);

        //Buscar empresas que coincidan con el nombre
        Page<Empresa> empresas = empresaService.findByNombreContaining(nombre, pageable);

        return ResponseEntity.ok(new PageResponseDTO<>(empresas));
    }
}
