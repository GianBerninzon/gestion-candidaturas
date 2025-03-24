package com.gestion_candidaturas.gestion_candidaturas.repository;

import com.gestion_candidaturas.gestion_candidaturas.model.Empresa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmpresaRepository extends JpaRepository<Empresa, UUID> {

    // Método personalizado para buscar empresas por nombre (parcial, ignorando mayúsculas/minúsculas)
    //List<Empresa> findByNombreContainingIgnoreCase(String nombre);
    // Metodo Paginacion
    Page<Empresa> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    // Verificar si existe una empresa con ese nombre exacto
    boolean existsByNombreIgnoreCase(String nombre);

    /**
     * Busca una empresa por su nombre exacto (ignorando mayúsculas/minúsculas).
     *
     * @param nombre Nombre exacto de la empresa a buscar
     * @return Optional con la empresa si existe, Optional vacío si no
     */
    Optional<Empresa> findByNombre(String nombre);

    /**
     * Busca una empresa por su nombre exacto, ignorando mayúsculas/minúsculas.
     *
     * @param nombre Nombre exacto de la empresa a buscar
     * @return Optional con la empresa si existe, Optional vacío si no
     */
    Optional<Empresa> findByNombreIgnoreCase(String nombre);

    /**
     * Consulta compleja que obtiene información resumida de usuarios que tienen candidaturas
     * asociadas a una empresa específica, junto con el conteo de candidaturas por usuario.
     *
     * @param empresaId ID de la empresa
     * @return Lista de arrays de objetos donde cada array contiene [id_usuario, username, contador_candidaturas]
     */
    @Query("SELECT u.id, u.username, COUNT(c) FROM User u JOIN Candidatura c ON u.id = c.user.id " +
            "WHERE c.empresa.id = :empresaId GROUP BY u.id, u.username")
    List<Object[]> findUsersByEmpresaId(@Param("empresaId") UUID empresaId);


}
