package com.gestion_candidaturas.gestion_candidaturas.service;

import com.gestion_candidaturas.gestion_candidaturas.model.Pregunta;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Interfaz que define las operaciones disponibles para la gestión de preguntas de entrevista.
 */
public interface PreguntaService {

    /**
     * Recupera todas las preguntas asociadas a una candidatura.
     *
     * @param candidaturaId ID de la candidatura.
     * @return Lista de preguntas asociadas a la candidatura.
     */
    List<Pregunta> findByCandidaturaId(UUID candidaturaId);

    /**
     * Busca una pregunta por su identificador único.
     *
     * @param id Identificador único de la pregunta.
     * @return Optional con la pregunta si existe, Optional vacío si no.
     */
    Optional<Pregunta> findById(UUID id);

    /**
     * Guarda una nueva pregunta o actualiza una existente.
     *
     * @param pregunta Entidad pregunta a guardar o actualizar.
     * @return La pregunta guardada con su ID generado si es nueva.
     */
    Pregunta save(Pregunta pregunta);

    /**
     * Elimina una pregunta por su identificador.
     *
     * @param id Identificador único de la pregunta a eliminar.
     * @return true si se eliminó correctamente, false si no existe.
     */
    boolean deleteById(UUID id);

    /**
     * Cuenta el número de preguntas asociadas a una candidatura.
     *
     * @param candidaturaId ID de la candidatura.
     * @return Número de preguntas asociadas.
     */
    int countByCandidaturaId(UUID candidaturaId);

    /**
     * Verifica si un usuario es propietario de una pregunta.
     *
     * @param preguntaId ID de la pregunta.
     * @param userId ID del usuario.
     * @return true si el usuario es propietario, false en caso contrario.
     */
    boolean isOwner(UUID preguntaId, UUID userId);

}
