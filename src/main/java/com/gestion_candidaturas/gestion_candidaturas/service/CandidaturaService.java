package com.gestion_candidaturas.gestion_candidaturas.service;

import com.gestion_candidaturas.gestion_candidaturas.model.Candidatura;
import com.gestion_candidaturas.gestion_candidaturas.model.EstadoCandidatura;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


/**
 * Interfaz que define las operaciones disponibles para la gestión de candidaturas.
 */
public interface CandidaturaService {

    /**
     * Recupera todas las candidaturas del sistema.
     *
     * @return Lista de todas las candidaturas.
     */
    List<Candidatura> findAll();

    /**
     * Recupera todas las candidaturas de un usuario específico.
     *
     * @param userId ID del usuario propietario.
     * @return Lista de candidaturas del usuario.
     */
    List<Candidatura> findByUserId(UUID userId);

    /**
     * Busca una candidatura por su identificador único.
     *
     * @param id Identificador único de la candidatura.
     * @return Optional con la candidatura si existe, Optional vacío si no.
     */
    Optional<Candidatura> findById(UUID id);

    /**
     * Guarda una nueva candidatura o actualiza una existente.
     *
     * @param candidatura Entidad candidatura a guardar o actualizar.
     * @return La candidatura guardada con su ID generado si es nueva.
     */
    Candidatura save(Candidatura candidatura);

    /**
     * Actualiza el estado de una candidatura existente.
     *
     * @param id ID de la candidatura.
     * @param estado Nuevo estado de la candidatura.
     * @return La candidatura actualizada o null si no existe.
     */
    Candidatura updateEstado(UUID id, EstadoCandidatura estado);

    /**
     * Elimina una candidatura por su identificador.
     *
     * @param id Identificador único de la candidatura a eliminar.
     * @return true si se eliminó correctamente, false si no existe.
     */
    boolean deleteById(UUID id);

    /**
     * Busca candidaturas por diversos criterios.
     *
     * @param estado Estado de la candidatura (opcional).
     * @param empresaNombre Nombre de la empresa (opcional).
     * @param fechaDesde Fecha inicial para filtrar (opcional).
     * @param fechaHasta Fecha final para filtrar (opcional).
     * @param q Texto de búsqueda general (opcional).
     * @param userId ID del usuario propietario.
     * @return Lista de candidaturas que cumplen los criterios.
     */
    List<Candidatura> buscar(EstadoCandidatura estado, String empresaNombre,
                             Date fechaDesde, Date fechaHasta, String q, UUID userId);

    /**
     * Verifica si un usuario es propietario de una candidatura.
     *
     * @param candidaturaId ID de la candidatura.
     * @param userId ID del usuario.
     * @return true si el usuario es propietario, false en caso contrario.
     */
    boolean isOwner(UUID candidaturaId, UUID userId);
}
