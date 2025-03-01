package com.gestion_candidaturas.gestion_candidaturas.model;

/**
 * Enumeración que define los posibles estados de una candidatura.
 *
 * @see RF-02: Permitir actualizar el estado de una candidatura.
 */
public enum EstadoCandidatura {
    PENDIENTE,
    ENTREVISTA,
    ACEPTADA,
    RECHAZADA,
    ARCHIVADA,
    EN_PROCESO
}
