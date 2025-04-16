package com.gestion_candidaturas.gestion_candidaturas.repository;

import com.gestion_candidaturas.gestion_candidaturas.model.Candidatura;
import com.gestion_candidaturas.gestion_candidaturas.model.EstadoCandidatura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.UUID;

/**
 * Repositorio que gestiona las operaciones de acceso a datos de candidaturas en la base de datos.
 * Extiende JpaRepository para heredar métodos CRUD básicos para la entidad Candidatura.
 */
public interface CandidaturaRepository extends JpaRepository<Candidatura, UUID> {

    /**
     * Busca todas las candidaturas pertenecientes a un usuario específico.
     * Las ordena por fecha de aplicación de más reciente a más antigua.
     *
     * @param userId ID del usuario propietario de las candidaturas
     * @return Lista de candidaturas del usuario ordenadas por fecha descendente
     */
    //List<Candidatura> findByUserIdOrderByFechaDesc(UUID userId);

    // Soporte Paginacion
    Page<Candidatura> findByUserIdOrderByFechaDesc(UUID userId, Pageable pageable);

    /**
     * Método avanzado que realiza una búsqueda de candidaturas con múltiples criterios opcionales.
     * Utiliza una consulta JPQL con las siguientes características:
     * - Cada parámetro es opcional (puede ser nulo)
     * - Si un parámetro es nulo, esa condición se ignora en la búsqueda
     * - Busca coincidencias parciales en texto usando LIKE con comodines
     * - Las búsquedas de texto son case-insensitive (ignoran mayúsculas/minúsculas)
     * - Siempre filtra por el usuario propietario (userId) para mantener la seguridad
     *
     * @param estado Estado de la candidatura (PENDIENTE, ENTREVISTA, etc.) - Opcional
     * @param empresaNombre Nombre o parte del nombre de la empresa - Opcional
     * @param fechaDesde Fecha mínima de aplicación - Opcional
     * @param fechaHasta Fecha máxima de aplicación - Opcional
     * @param q Texto general para buscar en cargo o notas - Opcional
     * @param userId ID del usuario propietario (obligatorio por seguridad)
     * @return Lista de candidaturas que cumplen todos los criterios especificados
     */
    //    @Query("SELECT c FROM Candidatura c WHERE " +
//            "(:estado IS NULL OR c.estado = :estado) AND " +
//            "(:empresaNombre IS NULL OR LOWER(c.empresa.nombre) LIKE LOWER(CONCAT('%', :empresaNombre, '%'))) AND " +
//            "(:fechaDesde IS NULL OR c.fecha >= :fechaDesde) AND " +
//            "(:fechaHasta IS NULL OR c.fecha <= :fechaHasta) AND " +
//            "(:q IS NULL OR LOWER(c.cargo) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(c.notas) LIKE LOWER(CONCAT('%', :q, '%'))) AND " +
//            "c.user.id = :userId " +
//            "ORDER BY c.fecha DESC")
    @Query("SELECT c FROM Candidatura c WHERE " +
            // Si estado es null, esta condición se ignora; si no, filtra por ese estado exacto
            "(:estado IS NULL OR c.estado = :estado) AND " +
            // Si empresaNombre es null, esta condición se ignora; si no, busca coincidencias parciales
            "(:empresaNombre IS NULL OR LOWER(c.empresa.nombre) LIKE LOWER(CONCAT('%', :empresaNombre, '%'))) AND " +
            // Si fechaDesde es null, esta condición se ignora; si no, filtra candidaturas posteriores a esa fecha
            "(:fechaDesde IS NULL OR c.fecha >= :fechaDesde) AND " +
            // Si fechaHasta es null, esta condición se ignora; si no, filtra candidaturas anteriores a esa fecha
            "(:fechaHasta IS NULL OR c.fecha <= :fechaHasta) AND " +
            // Si q es null, esta condición se ignora; si no, busca en cargo o notas
            "(:q IS NULL OR LOWER(c.cargo) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(c.notas) LIKE LOWER(CONCAT('%', :q, '%'))) AND " +
            // Siempre filtra por el usuario propietario para mantener seguridad
            "c.user.id = :userId " +
            "ORDER BY c.fecha DESC")
//    List<Candidatura> buscarCandidaturas(
//            @Param("estado")EstadoCandidatura estado,
//            @Param("empresaNombre") String espresaNombre,
//            @Param("fechaDesde")Date fechaDesde,
//            @Param("fechaHasta") Date fechaHasta,
//            @Param("q") String q,
//            @Param("userId") UUID userId
//            );
    Page<Candidatura> buscarCandidaturas(
            @Param("estado")EstadoCandidatura estado,
            @Param("empresaNombre") String espresaNombre,
            @Param("fechaDesde")Date fechaDesde,
            @Param("fechaHasta") Date fechaHasta,
            @Param("q") String q,
            @Param("userId") UUID userId,
            Pageable pageable
            );

    /**
     * Método que permite a los administradores buscar candidaturas de cualquier usuario.
     * Similar a buscarCandidaturas pero:
     * No filtra por un userId específico, permitiendo ver todas las candidaturas
     * Opcionalmente filtra por el userId si se proporciona (para filtrar por usuario)
     * Busca coincidencias en texto usando LIKE (en cargo, notas, nombre de empresa)
     * Las búsquedas son case-insensitive
     * 
     * @param q Texto de búsqueda general para cargo, notas o empresa - Opcional
     * @param userId ID del usuario propietario para filtrar (opcional para administradores)
     * @param pageable Objeto para paginación y ordenamiento
     * @return Página de candidaturas que cumplen los criterios especificados
     */
    @Query("SELECT c FROM Candidatura c WHERE " +
        // Si q es null, esta condición se ignora; si no, busca en cargo, notas o empresa
        "(:q IS NULL OR LOWER(c.cargo) LIKE LOWER(CONCAT('%', :q, '%')) " +
        "OR LOWER(c.notas) LIKE LOWER(CONCAT('%', :q, '%')) " + 
        "OR LOWER(c.empresa.nombre) LIKE LOWER(CONCAT('%', :q, '%'))) AND " +
        // Si userId es null, esta condición se ignora; si no, filtra por ese usuario
        "(:userId IS NULL OR c.user.id = :userId) " +
        "ORDER BY c.fecha DESC")
        Page<Candidatura> buscarCandidaturasAdmin(
            @Param("q") String q,
            @Param("userId") UUID userId,
            Pageable pageable);
    /**
     * Encuentra todas las candidaturas asociadas a un reclutador especifico.
     * 
     * @param reclutadorId ID del reclutador
     * @param pageable Ibjeto de paginacion
     * @return Pagina de candidaturas asociadas al reclutador
     */
    @Query("SELECT c FROM Candidatura c JOIN c.reclutadores r WHERE r.id = :reclutadorId")
    Page<Candidatura> findByReclutadoresId(@Param("reclutadorId") UUID reclutadorId, Pageable pageable);

    /**
     * Encuentra las candidaturas de un usuario especifico asociadas a un reclutador.
     * 
     * @param reclutadorId ID del reclutador
     * @param userId ID del usuario
     * @param pageable Ibjeto de paginacion
     * @return Pagina de candidaturas asociadas al usuario y reclutador
     */
    @Query("SELECT c FROM Candidatura c JOIN c.reclutadores r WHERE r.id = :reclutadorId AND c.user.id = :userId")
    Page<Candidatura> findByReclutadoresIdAndUserId(@Param("reclutadorId") UUID reclutadorId, @Param("userId") UUID userId, Pageable pageable);

}
