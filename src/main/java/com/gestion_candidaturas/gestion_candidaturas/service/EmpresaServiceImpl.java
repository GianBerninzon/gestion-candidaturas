package com.gestion_candidaturas.gestion_candidaturas.service;

import com.gestion_candidaturas.gestion_candidaturas.dto.EmpresaWithUsersDTO;
import com.gestion_candidaturas.gestion_candidaturas.dto.UserResumenDTO;
import com.gestion_candidaturas.gestion_candidaturas.model.Empresa;
import com.gestion_candidaturas.gestion_candidaturas.repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementación de la interfaz EmpresaService que proporciona
 * la lógica de negocio para la gestión de empresas.
 */
@Service
public class EmpresaServiceImpl implements EmpresaService{

    private final EmpresaRepository empresaRepository;

    @Autowired
    public EmpresaServiceImpl (EmpresaRepository empresaRepository){
        this.empresaRepository = empresaRepository;
    }


    /**
     * Obtiene todas las empresas del sistema.
     * Se utiliza @Transactional(readOnly = true) porque:
     * 1. Es una operación de solo lectura, no modifica datos
     * 2. Mejora el rendimiento al no necesitar seguimiento de cambios
     * 3. No requiere bloques de escritura en la base de datos
     * 4. Optimiza recursos al indicarle a Hibernate que no necesita trackear cambios
     */
    @Override
    @Transactional(readOnly = true)
    public List<Empresa> findAll() {
        return empresaRepository.findAll();
    }


    /**
     * Busca una empresa por su ID.
     * Se utiliza @Transactional(readOnly = true) porque:
     * 1. Es una operación de solo lectura
     * 2. No modifica ningún dato en la base de datos
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Empresa> findById(UUID id) {
        return empresaRepository.findById(id);
    }

    /**
     * Guarda o actualiza una empresa.
     * Se utiliza @Transactional (sin readOnly) porque:
     * 1. Es una operación que modifica datos
     * 2. Garantiza que toda la operación se realiza como una unidad atómica
     * 3. Si ocurre algún error, se hará rollback automático de todos los cambios
     * 4. Maneja correctamente la concurrencia y los bloqueos de la base de datos
     */
    @Override
    @Transactional
    public Empresa save(Empresa empresa) {
        return empresaRepository.save(empresa);
    }

    /**
     * Elimina una empresa por su ID.
     * Se utiliza @Transactional porque:
     * 1. Es una operación que modifica datos (eliminación)
     * 2. Podría afectar múltiples tablas debido a las relaciones
     * 3. Garantiza la integridad de los datos si la eliminación falla
     */
    @Override
    @Transactional
    public boolean deleteById(UUID id) {
        if (empresaRepository.existsById(id)) {
            empresaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Busca empresas que contienen el texto especificado en su nombre.
     * Se utiliza @Transactional(readOnly = true) porque:
     * 1. Es una operación de consulta que no modifica datos
     * 2. Optimiza el rendimiento para consultas complejas
     */
    @Override
    @Transactional(readOnly = true)
    public List<Empresa> findByNombreContaining(String nombre) {
        return empresaRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Empresa> findByNombre(String nombre) {
        // Utilizamos el método del repositorio para buscar por nombre exacto
        return empresaRepository.findByNombreIgnoreCase(nombre);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpresaWithUsersDTO> findAllWithAssociatedUsers() {
        // Obtenemos todas las empresas
        List<Empresa> empresas = empresaRepository.findAll();

        // Transformamos cada empresa en un DTO con información de usuarios asociados
        return empresas.stream().map(empresa -> {
            // Para cada empresa, obetenemos los usuarios asociados a través de candidaturas
            List<UserResumenDTO> usuariosAsociados = empresaRepository.findUsersByEmpresaId(empresa.getId())
                    .stream().map( resultado -> {
                        // Cada resultado es un array de objetos [UUID, String, Long] con [id, username, conteo]
                        Object[] datos = (Object[]) resultado;
                        return new UserResumenDTO(
                                (UUID) datos[0],                // ID del usuario
                                (String) datos[1],              // Username
                                ((Long) datos[2]).intValue()    // Número de candidaturas (convertido a int)
                        );
                    })
                    .collect(Collectors.toList());
            // Construimos el DTo completo
            return new EmpresaWithUsersDTO(
                    empresa.getId(),
                    empresa.getNombre(),
                    empresa.getCorreo(),
                    empresa.getTelefono(),
                    usuariosAsociados
            );
        }).collect(Collectors.toList());

    }
}
