package com.gestion_candidaturas.gestion_candidaturas.repository;

import com.gestion_candidaturas.gestion_candidaturas.model.Pregunta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface PreguntaRepository extends JpaRepository<Pregunta, UUID> {

    /**
     * Busca todas las preguntas asociadas a una candidatura específica.
     * Es útil para listar todas las preguntas preparadas para una entrevista.
     *
     * @param candidaturaId ID de la candidatura a la que pertenecen las preguntas
     * @return Lista de preguntas asociadas a la candidatura especificada
     */
    //List<Pregunta> findByCandidaturaId(UUID candidaturaId);
    // Soporte Paginacion
    Page<Pregunta> findByCandidaturaId(UUID candidaturaId, Pageable pageable);

    /**
     * Cuenta el número total de preguntas asociadas a una candidatura.
     * Este método es más eficiente que obtener todas las preguntas y contar,
     * ya que solo ejecuta un COUNT en la base de datos.
     *
     * @param candidaturaId ID de la candidatura a la que pertenecen las preguntas
     * @return Número total de preguntas asociadas a la candidatura
     */
    int countByCandidaturaId(UUID candidaturaId);

    /**
     * Verifica si una pregunta específica pertenece a un usuario concreto.
     * Utiliza una consulta JPQL con una expresión CASE para devolver directamente un boolean.
     * Es más eficiente que cargar la entidad completa solo para verificar el propietario.
     *
     * @param preguntaId ID de la pregunta a verificar
     * @param usuarioId ID del usuario que podría ser propietario
     * @return true si el usuario es propietario de la pregunta, false en caso contrario
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Pregunta p Where p.id = :preguntaId AND p.usuario.id = :usuarioId")
    boolean isPreguntaOwner(@Param("preguntaId") UUID preguntaId, @Param("usuarioId") UUID usuarioId);
}
