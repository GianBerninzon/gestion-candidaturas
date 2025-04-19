package com.gestion_candidaturas.gestion_candidaturas.service;

import com.gestion_candidaturas.gestion_candidaturas.model.Candidatura;
import com.gestion_candidaturas.gestion_candidaturas.model.EstadoCandidatura;
import com.gestion_candidaturas.gestion_candidaturas.repository.CandidaturaRepository;
import com.gestion_candidaturas.gestion_candidaturas.repository.PreguntaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementación de la interfaz CandidaturaService que proporciona
 * la lógica de negocio para la gestión de candidaturas.
 */
@Service
public class CandidaturaServiceImpl implements CandidaturaService{

    private final CandidaturaRepository candidaturaRepository;
    private final PreguntaRepository preguntaRepository;

    @Autowired
    public CandidaturaServiceImpl(CandidaturaRepository candidaturaRepository, PreguntaRepository preguntaRepository){
        this.candidaturaRepository = candidaturaRepository;
        this.preguntaRepository = preguntaRepository;
    }


//    @Override
//    @Transactional(readOnly = true)
//    public List<Candidatura> findAll() {
//        return candidaturaRepository.findAll();
//    }

//    @Override
//    @Transactional(readOnly = true)
//    public List<Candidatura> findByUserId(UUID userId) {
//        return candidaturaRepository.findByUserIdOrderByFechaDesc(userId);
//    }

    @Override
    @Transactional(readOnly = true)
    public Page<Candidatura> findAll(Pageable pageable) {
        return candidaturaRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Candidatura> findByUserId(UUID userId, Pageable pageable) {
        return candidaturaRepository.findByUserIdOrderByFechaDesc(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Candidatura> findById(UUID id) {
        return candidaturaRepository.findById(id);
    }

    @Override
    @Transactional
    public Candidatura save(Candidatura candidatura) {
        return candidaturaRepository.save(candidatura);
    }

    @Override
    @Transactional
    public Candidatura updateEstado(UUID id, EstadoCandidatura estado) {
        Optional<Candidatura> optionalCandidatura = candidaturaRepository.findById(id);
        if (optionalCandidatura.isPresent()) {
            Candidatura candidatura = optionalCandidatura.get();
            candidatura.setEstado(estado);
            return candidaturaRepository.save(candidatura);
        }
        return null;
    }

    @Override
    @Transactional
    public boolean deleteById(UUID id) {
        if (candidaturaRepository.existsById(id)) {
            try {
                // Eliminamos las preguntas asociadas
                preguntaRepository.deleteByCandidaturaId(id);

                // Eliminamos la candidatura
                candidaturaRepository.deleteById(id);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override
    public Page<Candidatura> buscar(EstadoCandidatura estado,
                                    String empresaNombre, Date fechaDesde,
                                    Date fechaHasta, String q, UUID userId,
                                    Pageable pageable) {
        return candidaturaRepository.buscarCandidaturas(
                estado, empresaNombre, fechaDesde, fechaHasta, q, userId, pageable
        );
    }

//    @Override
//    @Transactional(readOnly = true)
//    public List<Candidatura> buscar(EstadoCandidatura estado, String empresaNombre,
//                                    Date fechaDesde, Date fechaHasta, String q,
//                                    UUID userId) {
//            // Implementación de búsqueda con criterios
//            // Utilizamos un repositorio personalizado o especificación para la búsqueda
//            return candidaturaRepository.buscarCandidaturas(estado, empresaNombre,
//                    fechaDesde, fechaHasta, q, userId);
//    }

    @Override
    @Transactional(readOnly = true)
    public boolean isOwner(UUID candidaturaId, UUID userId) {
        Optional<Candidatura> candidatura = candidaturaRepository.findById(candidaturaId);

        // Verificar primero si la candidatura existe, y luego si el usuario es propietario
        return candidatura.isPresent() && candidatura.get().getUser().getId().equals(userId);

    }


    @Override
    @Transactional(readOnly = true)
    public Page<Candidatura> findByReclutadoresId(UUID reclutadorId, Pageable pageable) {
        return candidaturaRepository.findByReclutadoresId(reclutadorId, pageable);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<Candidatura> findByReclutadoresIdAndUserId(UUID reclutadorId, UUID userId, Pageable pageable) {
       return candidaturaRepository.findByReclutadoresIdAndUserId(reclutadorId, userId, pageable);
    }


    /**
     * Implementa la búsqueda de candidaturas para administradores, permitiendo filtrar
     * opcionalmente por palabra clave y/o usuario específico.
     * Si no se proporciona un userId, devuelve candidaturas de todos los usuarios.
     *
     * @param q Texto de búsqueda para filtrar por cargo, notas o empresa
     * @param userId ID de usuario específico (opcional)
     * @param pageable Objeto de paginación
     * @return Página de candidaturas que cumplen con los criterios
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Candidatura> buscarAdmin(String q, UUID userId, Pageable pageable) {
        return candidaturaRepository.buscarCandidaturasAdmin(q, userId, pageable);
    }


    @Override
    @Transactional
    public int deleteAllByIds(List<UUID> ids) {
       int count = 0;

       // Filtramos solo los IDs que existen
       List<UUID> existingIds = ids.stream()
                                  .filter(candidaturaRepository::existsById)
                                  .toList();

       if(!existingIds.isEmpty()){
        try {
            // Primero eliminamos todas las preguntas asociadas a estas candidaturas
            preguntaRepository.deleteByCandidaturaIdIn(existingIds);

            // Luego eliminamos las candidaturas
            for(UUID id: existingIds){
                candidaturaRepository.deleteById(id);
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
       }

       return count;
    }


    @Override
    @Transactional(readOnly = true)
    public boolean existsByUserIdAndEmpresaId(UUID userId, UUID empresaId) {
        return candidaturaRepository.existsByUserIdAndEmpresaId(userId, empresaId);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<Candidatura> findByEmpresaId(UUID empresaId, Pageable pageable) {
        return candidaturaRepository.findByEmpresaId(empresaId, pageable);
    }

    
}
