package com.gestion_candidaturas.gestion_candidaturas.service;

import com.gestion_candidaturas.gestion_candidaturas.model.Reclutador;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Interfaz que define las operaciones disponibles para la gestión de reclutadores.
 */
public interface ReclutadorService {
    /**
     * Recupera todos los reclutadores del sistema.
     *
     * @return Lista de todos los reclutadores.
     */
    List<Reclutador> findAll();

    /**
     * Busca un reclutador por su identificador único.
     *
     * @param id Identificador único del reclutador.
     * @return Optional con el reclutador si existe, Optional vacío si no.
     */
    Optional<Reclutador> findById(UUID id);

    /**
     * Recupera todos los reclutadores asociados a una empresa.
     *
     * @param empresaId ID de la empresa.
     * @return Lista de reclutadores asociados a la empresa.
     */
    List<Reclutador> findByEmpresaId(UUID empresaId);

    /**
     * Busca un reclutador por su nombre y empresa.
     *
     * @param nombre Nombre del reclutador
     * @param empresaId ID de la empresa
     * @return Optional con el reclutador si existe, Optional vacío si no
     */
    Optional<Reclutador> findByNombreAndEmpresaId(String nombre, UUID empresaId);

    /**
     * Verifica si un reclutador está asociado a alguna candidatura del usuario especificado.
     *
     * @param reclutadorId ID del reclutador
     * @param userId ID del usuario
     * @return true si el reclutador está asociado a alguna candidatura del usuario, false en caso contrario
     */
    boolean isReclutadorAssociatedWithUserCandidaturas(UUID reclutadorId, UUID userId);

    /**
     * Guarda un nuevo reclutador o actualiza uno existente.
     *
     * @param reclutador Entidad reclutador a guardar o actualizar.
     * @return El reclutador guardado con su ID generado si es nuevo.
     */
    Reclutador save(Reclutador reclutador);

    /**
     * Elimina un reclutador por su identificador.
     *
     * @param id Identificador único del reclutador a eliminar.
     * @return true si se eliminó correctamente, false si no existe.
     */
    boolean deleteById(UUID id);

    /**
     * Asocia un reclutador a una candidatura.
     *
     * @param reclutadorId ID del reclutador.
     * @param candidaturaId ID de la candidatura.
     * @return true si se asoció correctamente, false si no.
     */
    boolean asociarACandidatura(UUID reclutadorId, UUID candidaturaId);

    /**
     * Desasocia un reclutador de una candidatura.
     *
     * @param reclutadorId ID del reclutador.
     * @param candidaturaId ID de la candidatura.
     * @return true si se desasoció correctamente, false si no.
     */
    boolean desasociarDeCandidatura(UUID reclutadorId, UUID candidaturaId);
}
