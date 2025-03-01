package com.gestion_candidaturas.gestion_candidaturas.service;

import com.gestion_candidaturas.gestion_candidaturas.model.Candidatura;
import com.gestion_candidaturas.gestion_candidaturas.model.EstadoCandidatura;
import com.gestion_candidaturas.gestion_candidaturas.repository.CandidaturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public CandidaturaServiceImpl(CandidaturaRepository candidaturaRepository){
        this.candidaturaRepository = candidaturaRepository;
    }


    @Override
    @Transactional(readOnly = true)
    public List<Candidatura> findAll() {
        return candidaturaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Candidatura> findByUserId(UUID userId) {
        return candidaturaRepository.findByUserIdOrderByFechaDesc(userId);
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
            candidaturaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Candidatura> buscar(EstadoCandidatura estado, String empresaNombre,
                                    Date fechaDesde, Date fechaHasta, String q,
                                    UUID userId) {
            // Implementación de búsqueda con criterios
            // Utilizamos un repositorio personalizado o especificación para la búsqueda
            return candidaturaRepository.buscarCandidaturas(estado, empresaNombre,
                    fechaDesde, fechaHasta, q, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isOwner(UUID candidaturaId, UUID userId) {
        Optional<Candidatura> candidatura = candidaturaRepository.findById(candidaturaId);

        // Verificar primero si la candidatura existe, y luego si el usuario es propietario
        return candidatura.isPresent() && candidatura.get().getUser().getId().equals(userId);

    }
}
