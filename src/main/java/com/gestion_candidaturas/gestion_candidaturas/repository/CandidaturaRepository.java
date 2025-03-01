package com.gestion_candidaturas.gestion_candidaturas.repository;

import com.gestion_candidaturas.gestion_candidaturas.model.Candidatura;
import com.gestion_candidaturas.gestion_candidaturas.model.EstadoCandidatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
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
    List<Candidatura> findByUserIdOrderByFechaDesc(UUID userId);

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
    @Query("SELECT c FROM Candidatura c WHERE " +
            // Si estado es null, esta condición se ignora; si no, filtra por ese estado exacto
            "(:estado IS NULL OR c.estado = :estado) AND " +
            // Si empresaNombre es null, esta condición se ignora; si no, busca coincidencias parciales
            "(:empresa IS NULL OR LOWER(c.empresa.nombre) LIKE LOWER (CONCAT('%', :empresaNombre, '%'))) AND" +
            // Si fechaDesde es null, esta condición se ignora; si no, filtra candidaturas posteriores a esa fecha
            "(:fechaDesde IS NULL OR c.fecha >= :fechaDesde) AND " +
            // Si fechaHasta es null, esta condición se ignora; si no, filtra candidaturas anteriores a esa fecha
            "(:fechaHasta IS NULL OR c.fecha >= fechaHasta) AND " +
            // Si q es null, esta condición se ignora; si no, busca en cargo o notas
            "(:q IS NULL OR LOWER(c.cargo) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(c.notas) LIKE LOWER('%', :q, '%'))) AND" +
            // Siempre filtra por el usuario propietario para mantener seguridad
            "c.user.id = :userId")
    List<Candidatura> buscarCandidaturas(
            @Param("estado")EstadoCandidatura estado,
            @Param("empresaNombre") String espresaNombre,
            @Param("fechaDesde")Date fechaDesde,
            @Param("fechaHasta") Date fechaHasta,
            @Param("q") String q,
            @Param("userId") UUID userId
            );

}
