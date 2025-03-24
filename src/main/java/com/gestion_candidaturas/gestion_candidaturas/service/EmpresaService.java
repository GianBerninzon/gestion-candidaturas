package com.gestion_candidaturas.gestion_candidaturas.service;


import com.gestion_candidaturas.gestion_candidaturas.dto.EmpresaWithUsersDTO;
import com.gestion_candidaturas.gestion_candidaturas.model.Empresa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Interfaz que define las operaciones disponibles para la gestión de empresas.
 */
public interface EmpresaService {

    /**
     * Recupera todas las empresas del sistema.
     *
     * @return Lista de todas las empresas.
     */
    //List<Empresa> findAll();
    // Metodo paginacion
    Page<Empresa> findAll(Pageable pageable);

    /**
     * Busca una empresa por su identificador único.
     *
     * @param id Identificador único de la empresa.
     * @return Optional con la empresa si existe, Optional vacío si no.
     */
    Optional<Empresa> findById(UUID id);

    /**
     * Guarda una nueva empresa o actualiza una existente.
     *
     * @param empresa Entidad empresa a guardar o actualizar.
     * @return La empresa guardada con su ID generado si es nueva.
     */
    Empresa save(Empresa empresa);

    /**
     * Elimina una empresa por su identificador.
     *
     * @param id Identificador único de la empresa a eliminar.
     * @return true si se eliminó correctamente, false si no existe.
     */
    boolean deleteById(UUID id);

    /**
     * Busca empresas por nombre (búsqueda parcial).
     *
     * @param nombre Texto a buscar en el nombre de la empresa.
     * @return Lista de empresas cuyo nombre contiene el texto buscado.
     */
    //List<Empresa> findByNombreContaining(String nombre);
    //Metodo paginacion
    Page<Empresa> findByNombreContaining(String nombre, Pageable pageable);

    /**
     * Busca una empresa por su nombre exacto (ignorando mayúsculas/minúsculas).
     *
     * @param nombre Nombre exacto de la empresa a buscar
     * @return Optional con la empresa si existe, Optional vacío si no
     */
    Optional<Empresa> findByNombre(String nombre);

    /**
     * Obtiene todas las empresas con información de los usuarios asociados a ellas.
     * Este método es exclusivo para administradores y ofrece una visión completa
     * de qué usuarios están aplicando a qué empresas.
     *
     * @return Lista de empresas con información de usuarios asociados
     */
    List<EmpresaWithUsersDTO> findAllWithAssociatedUsers();
}
