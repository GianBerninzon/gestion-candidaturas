package com.gestion_candidaturas.gestion_candidaturas.controller;

import com.gestion_candidaturas.gestion_candidaturas.dto.PageResponseDTO;
import com.gestion_candidaturas.gestion_candidaturas.dto.PreguntaDTO;
import com.gestion_candidaturas.gestion_candidaturas.model.Candidatura;
import com.gestion_candidaturas.gestion_candidaturas.model.Pregunta;
import com.gestion_candidaturas.gestion_candidaturas.model.User;
import com.gestion_candidaturas.gestion_candidaturas.service.CandidaturaService;
import com.gestion_candidaturas.gestion_candidaturas.service.PreguntaService;
import com.gestion_candidaturas.gestion_candidaturas.service.UserService;
import com.gestion_candidaturas.gestion_candidaturas.util.PaginacionUtil;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

/**
 * Controlador REST para operaciones relacionadas con preguntas de entrevista.
 *
 * @see RF-06: Permitir agregar preguntas de entrevista
 * @see RF-07: Mostrar número total de preguntas
 */
@RestController
@RequestMapping("/api/preguntas")
public class PreguntaController {

    private final PreguntaService preguntaService;
    private final UserService userService;
    private final CandidaturaService candidaturaService;

    /**
     * Constructor para inyección de dependencias.
     *
     * @param preguntaService Servicio para operaciones con preguntas
     * @param userService Servicio para operaciones con usuarios
     * @param candidaturaService Servicio para operaciones con candidaturas
     */
    public PreguntaController(PreguntaService preguntaService, UserService userService,
                              CandidaturaService candidaturaService){
        this.preguntaService = preguntaService;
        this.userService = userService;
        this.candidaturaService = candidaturaService;
    }

    /**
     * Obtiene todas las preguntas asociadas a una candidatura específica.
     *
     * @param candidaturaId ID de la candidatura
     * @return Lista de preguntas de la candidatura
     *
     * @see RF-06: Consulta de preguntas por candidatura
     */
//    @GetMapping
//    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ROOT')")
//    public ResponseEntity<List<Pregunta>> getPreguntasByCandidatura(
//            @RequestParam UUID candidaturaId) {
//        List<Pregunta> preguntas = preguntaService.findByCandidaturaId(candidaturaId);
//        return ResponseEntity.ok(preguntas);
//    }

    /**
     * Obtiene todas las preguntas asociadas a una candidatura específica con soporte para paginación.
     *
     * @param candidaturaId ID de la candidatura
     * @param page Número de página (0-indexed)
     * @param size Tamaño de la página
     * @param sort Campos y direcciones de ordenamiento
     * @return ResponseEntity con la página de preguntas de la candidatura
     *
     * @see RF-06: Consulta de preguntas por candidatura con paginación
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ROOT')")
    public ResponseEntity<PageResponseDTO<Pregunta>> getPreguntasByCandidatura(
            @RequestParam UUID candidaturaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort){
        //Crear objeto Pageable con la informacion de paginacion y ordenamiento
        Pageable pageable = PaginacionUtil.crearPageable(page, size, sort);

        //Obtener preguntas paginadas
        Page<Pregunta> preguntas = preguntaService.findByCandidaturaId(candidaturaId, pageable);

        return ResponseEntity.ok(new PageResponseDTO<>(preguntas));
    }

    /**
     * Obtiene el número total de preguntas para una candidatura.
     *
     * @param candidaturaId ID de la candidatura
     * @return El número de preguntas registradas
     *
     * @see RF-07: Conteo de preguntas por candidatura
     */
    @GetMapping("/count")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ROOT')")
    public ResponseEntity<Integer> countPreguntasByCandidatura(
            @RequestParam UUID candidaturaId) {
        int count = preguntaService.countByCandidaturaId(candidaturaId);
        return ResponseEntity.ok(count);
    }

    /**
     * Crea una nueva pregunta asociada a una candidatura y al usuario actual.
     *
     * @param preguntaDTO Datos de la pregunta a crear
     * @return La pregunta creada
     *
     * @see RF-06: Registro de preguntas de entrevista
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ROOT')")
    public ResponseEntity<Pregunta> createPregunta(@Valid @RequestBody PreguntaDTO preguntaDTO) {
        // Obtener el usuario actual
        User currentUser = userService.getCurrentUser();

        // Buscar la candidatura por ID
        Optional<Candidatura> candidaturaOpt = candidaturaService.findById(preguntaDTO.getCandidaturaId());
        if (candidaturaOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Crear la nueva pregunta
        Pregunta pregunta = new Pregunta();
        pregunta.setUsuario(currentUser);
        pregunta.setCandidatura(candidaturaOpt.get());
        pregunta.setPregunta(preguntaDTO.getPregunta());

        // Guardar la pregunta
        Pregunta nuevaPregunta = preguntaService.save(pregunta);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaPregunta);
    }

    /**
     * Actualiza una pregunta existente.
     * Los usuarios normales solo pueden actualizar sus propias preguntas.
     * Los administradores pueden actualizar cualquier pregunta.
     *
     * @param id ID de la pregunta a actualizar
     * @param preguntaDTO Datos actualizados
     * @return La pregunta actualizada, 404 si no existe, 403 si no tiene permisos
     *
     * @see RF-06: Modificación de preguntas de entrevista
     * @see RF-11: Control de acceso basado en roles
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ROOT')")
    public ResponseEntity<Pregunta> updatePregunta(@PathVariable UUID id,
                                                   @Valid @RequestBody PreguntaDTO preguntaDTO) {
        // Buscar la pregunta existente
        Optional<Pregunta> preguntaExistente = preguntaService.findById(id);

        if (preguntaExistente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Obtener el usuario actual
        User currentUser = userService.getCurrentUser();

        // Verificar permisos de modificación
        if (preguntaExistente.get().getUsuario().getId().equals(currentUser.getId()) ||
                currentUser.hasRole("ADMIN") || currentUser.hasRole("ROOT")) {

            // Buscar la candidatura por ID
            Optional<Candidatura> candidaturaOpt = candidaturaService.findById(preguntaDTO.getCandidaturaId());
            if (candidaturaOpt.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            // Actualizar la pregunta
            Pregunta pregunta = preguntaExistente.get();
            pregunta.setPregunta(preguntaDTO.getPregunta());
            pregunta.setCandidatura(candidaturaOpt.get());

            // Preservar la información que no debería cambiar
            pregunta.setId(id);
            pregunta.setUsuario(preguntaExistente.get().getUsuario());

            // Guardar cambios
            Pregunta preguntaActualizada = preguntaService.save(pregunta);
            return ResponseEntity.ok(preguntaActualizada);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Elimina una pregunta de entrevista.
     * Los usuarios normales solo pueden eliminar sus propias preguntas.
     * Los administradores pueden eliminar cualquier pregunta.
     *
     * @param id ID de la pregunta a eliminar
     * @return 204 si se eliminó correctamente, 404 si no existe, 403 si no tiene permisos
     *
     * @see RF-06: Eliminación de preguntas de entrevista
     * @see RF-11: Control de acceso basado en roles
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ROOT')")
    public ResponseEntity<Void> deletePregunta(@PathVariable UUID id) {
        // Buscar la pregunta existente
        Optional<Pregunta> preguntaExistente = preguntaService.findById(id);

        if (preguntaExistente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Obtener el usuario actual
        User currentUser = userService.getCurrentUser();

        // Verificar permisos de eliminación
        if (preguntaExistente.get().getUsuario().getId().equals(currentUser.getId()) ||
                currentUser.hasRole("ADMIN") || currentUser.hasRole("ROOT")) {

            // Eliminar la pregunta
            preguntaService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
