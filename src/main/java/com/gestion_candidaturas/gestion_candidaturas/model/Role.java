package com.gestion_candidaturas.gestion_candidaturas.model;

/**
 * Enumeración que define los roles de usuario disponibles en el sistema.
 *
 * @see RF-11: Control de acceso basado en roles para operaciones CRUD.
 */
public enum Role {
    /**
     * Usuario regular. Puede gestionar solo sus propias candidaturas y preguntas.
     */
    USER,

    /**
     * Administrador. Tiene permisos extendidos sobre todas las entidades excepto usuarios ROOT.
     */
    ADMIN,

    /**
     * Administrador del sistema. Tiene control total sobre todas las funcionalidades.
     */
    ROOT
}
