package com.gestion_candidaturas.gestion_candidaturas.service;

import com.gestion_candidaturas.gestion_candidaturas.model.Candidatura;
import com.gestion_candidaturas.gestion_candidaturas.model.EstadoCandidatura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    //List<Candidatura> findAll();
    // Metodo Paginacion
    Page<Candidatura> findAll(Pageable pageable);

    /**
     * Recupera todas las candidaturas de un usuario específico.
     *
     * @param userId ID del usuario propietario.
     * @return Lista de candidaturas del usuario.
     */
    //List<Candidatura> findByUserId(UUID userId);
    // Metodo Paginacion
    Page<Candidatura> findByUserId(UUID userId, Pageable pageable);

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
    //List<Candidatura> buscar(EstadoCandidatura estado, String empresaNombre,
    //                        Date fechaDesde, Date fechaHasta, String q, UUID userId);
    //Metodo Paginacion
    Page<Candidatura> buscar(
            EstadoCandidatura estado, String empresaNombre,
            Date fechaDesde, Date fechaHasta, String q, UUID userId, Pageable pageable);

    /**
     * Busca candidaturas para administradores con filtros opcionales.
     *  A diferencia del método buscar estándar, este no restringe los resultados
     *  a un usuario específico a menos que se proporcione el parámetro userId.
     * 
     *  @param q Texto de búsqueda general para filtrar por cargo, notas o empresa (opcional).
     * @param userId ID del usuario para filtrar (opcional, si es null muestra todas las candidaturas).
     * @param pageable Objeto de paginación para controlar el tamaño y número de página.
     * @return Página de candidaturas que cumplen los criterios especificados.
     */
    Page<Candidatura> buscarAdmin(String q, UUID userId, Pageable pageable);

    /**
     * Verifica si un usuario es propietario de una candidatura.
     *
     * @param candidaturaId ID de la candidatura.
     * @param userId ID del usuario.
     * @return true si el usuario es propietario, false en caso contrario.
     */
    boolean isOwner(UUID candidaturaId, UUID userId);

    /**
     * Encuentra todas las candidaturas asociadas a un reclutador especifico.
     * 
     * @param reclutadorId ID del reclutador
     * @param pageable Objeto de paginacion
     * @return Lista de candidaturas asociadas al reclutador
     */
    Page<Candidatura> findByReclutadoresId(UUID reclutadorId, Pageable pageable);

    /**
     * Encuentra las candidaturas asociadas a un reclutador especifico que pertenecen a un usuario especifico.
     * 
     * @param reclutadorId ID del reclutador
     * @param userId ID del usuario
     * @param pageable Objeto de paginacion
     * @return lista de candidaturas del usuario asociadas al reclutador
     */
    Page<Candidatura> findByReclutadoresIdAndUserId(UUID reclutadorId, UUID userId, Pageable pageable);

    /**
     * Elimina multiples candidaturas por sis identificadores.
     * 
     * @param ids Lista de identificadores unicos de las candidaturas a eliminar.
     * @return El numero de candidaturas eliminadas.
     */
    int deleteAllByIds(List<UUID> ids);

    /**
     * Verifica si un usuario tiene candidaturas asociadas a una empresa específica.
     * 
     * @param userId ID del usuario
     * @param empresaId ID de la empresa
     * @return true si el usuario tiene candidaturas asociadas a la empresa, false en caso contrario
     */
    boolean existsByUserIdAndEmpresaId(UUID userId, UUID empresaId);

    /**
     * Recupera todas las candidaturas asociadas a una empresa especifica.
 
     * @param empresaId ID de la empresa
     * @return Lista de candidaturas asociadas a la empresa
     */
    Page<Candidatura> findByEmpresaId(UUID empresaId, Pageable pageable);
}
