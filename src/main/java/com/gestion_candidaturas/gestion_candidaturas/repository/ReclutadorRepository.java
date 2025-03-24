package com.gestion_candidaturas.gestion_candidaturas.repository;

import com.gestion_candidaturas.gestion_candidaturas.model.Reclutador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReclutadorRepository extends JpaRepository<Reclutador, UUID> {

    // Obtener todos los reclutadores de una empresa específica
    //List<Reclutador> findByEmpresaId(UUID empresaId);

    //Soporte Paginacion
    Page<Reclutador> findByEmpresaId(UUID empresaId, Pageable pageable);

    // Verificar si existe un reclutador con ese nombre en esa empresa
    boolean existsByNombreAndEmpresaId(String nombre, UUID empresaId);

    /**
     * Busca un reclutador por su nombre exacto y la empresa a la que pertenece.
     *
     * @param nombre Nombre del reclutador
     * @param empresaId ID de la empresa
     * @return Optional con el reclutador si existe, Optional vacío si no
     */
    Optional<Reclutador> findByNombreAndEmpresaId(String nombre, UUID empresaId);
}
