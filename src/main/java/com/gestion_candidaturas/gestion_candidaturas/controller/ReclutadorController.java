package com.gestion_candidaturas.gestion_candidaturas.controller;


import com.gestion_candidaturas.gestion_candidaturas.model.Reclutador;
import com.gestion_candidaturas.gestion_candidaturas.service.ReclutadorService;
import com.gestion_candidaturas.gestion_candidaturas.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Controlador REST para operaciones relacionadas con reclutadores.
 *
 * @see RF-04: Permitir agregar reclutadores
 * @see RF-05: Relacionar reclutadores con candidaturas
 */
@RestController
@RequestMapping("/api/reclutador")
public class ReclutadorController {

    private final ReclutadorService reclutadorService;
    private final UserService userService;

    /**
     * Constructor para inyección de dependencias.
     *
     * @param reclutadorService Servicio para operaciones con reclutadores
     */
    public ReclutadorController(ReclutadorService reclutadorService, UserService userService){
        this.reclutadorService = reclutadorService;
        this.userService = userService;
    }

    /**
     * Obtiene todos los reclutadores del sistema.
     *
     * @return Lista de reclutadores
     *
     * @see RF-04: Consulta de reclutadores
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ROOT')")
    public ResponseEntity<List<Reclutador>> getAllReclutadores(){
        return ResponseEntity.ok(reclutadorService.findAll());
    }

    /**
     * Obtiene un reclutador específico por su ID.
     *
     * @param id ID del reclutador
     * @return El reclutador si existe, 404 si no
     *
     * @see RF-04: Consulta de reclutador específico
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ROOT')")
    public ResponseEntity<Reclutador> getReclutadorById(@PathVariable UUID id) {
        Optional<Reclutador> reclutador = reclutadorService.findById(id);
        return reclutador.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtiene todos los reclutadores asociados a una empresa específica.
     *
     * @param empresaId ID de la empresa
     * @return Lista de reclutadores de la empresa
     *
     * @see RF-04: Filtrado de reclutadores por empresa
     */
    @GetMapping("/empresa/{empresaId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ROOT')")
    public ResponseEntity<List<Reclutador>> getReclutadoresByEmpresa(@PathVariable UUID empresaId) {
        List<Reclutador> reclutadores = reclutadorService.findByEmpresaId(empresaId);
        return ResponseEntity.ok(reclutadores);
    }

    /**
     * Crea un nuevo reclutador.
     *
     * @param reclutador Datos del reclutador a crear
     * @return El reclutador creado
     *
     * @see RF-04: Registro de reclutadores
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ROOT')")
    public ResponseEntity<Reclutador> createReclutador(@Valid @RequestBody Reclutador reclutador) {
        Reclutador nuevoReclutador = reclutadorService.save(reclutador);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoReclutador);
    }

    /**
     * Endpoint especializado para crear reclutadores durante el proceso de registro o actualización de candidaturas.
     * Este endpoint permite a usuarios normales crear reclutadores, pero solo en el contexto de gestionar candidaturas.
     * Implementa una lógica de "buscar primero, crear si no existe" para evitar duplicados.
     *
     * @param reclutador Datos del reclutador a crear o buscar
     * @return El reclutador existente o el nuevo reclutador creado
     *
     * @see RF-04: Permitir a usuarios crear reclutadores durante la gestión de candidaturas
     */
    @PostMapping("/crear-con-candidatura")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Reclutador> createReclutadorForCandidatura(@Valid @RequestBody Reclutador reclutador){
        // Verificar si ya existe un reclutador con el mismo nombre en la misma empresa
        Optional<Reclutador> existingReclutador =reclutadorService.findByNombreAndEmpresaId(
                reclutador.getNombre(), reclutador.getEmpresa().getId());

        if(existingReclutador.isEmpty()){
            // Si el reclutador ya existe, lo retornamos para ser usado en la candidatura
            return ResponseEntity.ok(existingReclutador.get());
        }

        // Si no existe, creamos el nuevo reclutador
        Reclutador nuevoReclutador = reclutadorService.save(reclutador);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoReclutador);
    }

    /**
     * Endpoint especializado para actualizar reclutadores creados por el usuario durante el proceso
     * de gestión de candidaturas. Los usuarios solo pueden actualizar reclutadores que hayan
     * creado y que estén asociados a sus propias candidaturas.
     *
     * @param id ID del reclutador a actualizar
     * @param reclutador Datos actualizados
     * @return El reclutador actualizado, 404 si no existe, 403 si no tiene permisos
     *
     * @see RF-04: Permitir a usuarios actualizar reclutadores asociados a sus candidaturas
     */
    @PutMapping("/{id}/user-update")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Reclutador> updateReclutadorByUser(@PathVariable UUID id,
                                                             @Valid @RequestBody Reclutador reclutador){
        // Verificar si el reclutador existe
        Optional<Reclutador> existingReclutador = reclutadorService.findById(id);
        if(existingReclutador.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        //Verificar si el usuario tiene permiso para actualizar este reclutador
        // (si está asociado a alaguna de sus candidaturas)
        if(!reclutadorService.isReclutadorAssociatedWithUserCandidaturas(id,
                userService.getCurrentUser().getId())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Mantener el ID y la empresa original
        reclutador.setId(id);
        reclutador.setEmpresa(existingReclutador.get().getEmpresa());

        //  Guardar los cambios
        Reclutador reclutadorActualizado = reclutadorService.save(reclutador);
        return ResponseEntity.ok(reclutadorActualizado);
    }

    /**
     * Actualiza un reclutador existente.
     *
     * @param id ID del reclutador a actualizar
     * @param reclutador Datos actualizados
     * @return El reclutador actualizado, 404 si no existe
     *
     * @see RF-04: Actualización de información de reclutadores
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ROOT')")
    public ResponseEntity<Reclutador> updateReclutador(@PathVariable UUID id,
                                                       @Valid @RequestBody Reclutador reclutador) {
        if (reclutadorService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        reclutador.setId(id);
        Reclutador reclutadorActualizado = reclutadorService.save(reclutador);
        return ResponseEntity.ok(reclutadorActualizado);
    }

    /**
     * Elimina un reclutador.
     *
     * @param id ID del reclutador a eliminar
     * @return 204 si se eliminó correctamente, 404 si no existe
     *
     * @see RF-04: Eliminación de reclutadores
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ROOT')")
    public ResponseEntity<Void> deleteReclutador(@PathVariable UUID id) {
        boolean eliminado = reclutadorService.deleteById(id);
        return eliminado ? ResponseEntity.noContent().build() :
                ResponseEntity.notFound().build();
    }

    /**
     * Asocia un reclutador a una candidatura.
     *
     * @param reclutadorId ID del reclutador
     * @param candidaturaId ID de la candidatura
     * @return 200 si se asoció correctamente, 404 si alguna entidad no existe
     *
     * @see RF-05: Relacionar reclutadores con candidaturas
     */
    @PostMapping("/{reclutadorId}/candidaturas/{candidaturaId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ROOT')")
    public ResponseEntity<Void> asociarACandidatura(@PathVariable UUID reclutadorId,
                                                    @PathVariable UUID candidaturaId) {
        boolean asociado = reclutadorService.asociarACandidatura(reclutadorId, candidaturaId);
        return asociado ? ResponseEntity.ok().build() :
                ResponseEntity.notFound().build();
    }

    /**
     * Desasocia un reclutador de una candidatura.
     *
     * @param reclutadorId ID del reclutador
     * @param candidaturaId ID de la candidatura
     * @return 200 si se desasoció correctamente, 404 si alguna entidad no existe
     *
     * @see RF-05: Desasociar reclutadores de candidaturas
     */
    @DeleteMapping("/{reclutadorId}/candidaturas/{candidaturaId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ROOT')")
    public ResponseEntity<Void> desasociarDeCandidatura(@PathVariable UUID reclutadorId,
                                                        @PathVariable UUID candidaturaId) {
        boolean desasociado = reclutadorService.desasociarDeCandidatura(reclutadorId, candidaturaId);
        return desasociado ? ResponseEntity.ok().build() :
                ResponseEntity.notFound().build();
    }
}
