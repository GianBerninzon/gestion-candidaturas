package com.gestion_candidaturas.gestion_candidaturas.service;

import com.gestion_candidaturas.gestion_candidaturas.model.Candidatura;
import com.gestion_candidaturas.gestion_candidaturas.model.Reclutador;
import com.gestion_candidaturas.gestion_candidaturas.repository.CandidaturaRepository;
import com.gestion_candidaturas.gestion_candidaturas.repository.ReclutadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementación de la interfaz ReclutadorService que proporciona
 * la lógica de negocio para la gestión de reclutadores.
 */
@Service
public class ReclutadorServiceImpl implements ReclutadorService{

    private final ReclutadorRepository reclutadorRepository;

    private final CandidaturaRepository candidaturaRepository;

    @Autowired
    public  ReclutadorServiceImpl(ReclutadorRepository reclutadorRepository,
                                  CandidaturaRepository candidaturaRepository){
        this.reclutadorRepository = reclutadorRepository;
        this.candidaturaRepository = candidaturaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reclutador> findAll() {
        return reclutadorRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Reclutador> findById(UUID id) {
        return reclutadorRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reclutador> findByEmpresaId(UUID empresaId) {
        return reclutadorRepository.findByEmpresaId(empresaId);
    }

    @Override
    public Optional<Reclutador> findByNombreAndEmpresaId(String nombre, UUID empresaId) {
        return reclutadorRepository.findByNombreAndEmpresaId(nombre, empresaId);
    }

    @Override
    public boolean isReclutadorAssociatedWithUserCandidaturas(UUID reclutadorId, UUID userId) {
        // Verificamos si el reclutador existe
        Optional<Reclutador> reclutadorOpt = reclutadorRepository.findById(reclutadorId);
        if(reclutadorOpt.isEmpty()){
            return false;
        }

        // Obtenemos el reclutador y sus candidaturas asociadas
        Reclutador reclutador = reclutadorOpt.get();

        // Verificamos si alguna de esas candidaturas pertenece al usuario
        return reclutador.getCandidaturas().stream().anyMatch(
                candidatura -> candidatura.getUser().getId().equals(userId));
    }

    @Override
    @Transactional
    public Reclutador save(Reclutador reclutador) {
        return reclutadorRepository.save(reclutador);
    }

    @Override
    @Transactional
    public boolean deleteById(UUID id) {
        if (reclutadorRepository.existsById(id)) {
            reclutadorRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean asociarACandidatura(UUID reclutadorId, UUID candidaturaId) {
        Optional<Reclutador> reclutadorOpt = reclutadorRepository.findById(reclutadorId);
        Optional<Candidatura> candidaturaOpt = candidaturaRepository.findById(candidaturaId);

        if (reclutadorOpt.isPresent() && candidaturaOpt.isPresent()) {
            Reclutador reclutador = reclutadorOpt.get();
            Candidatura candidatura = candidaturaOpt.get();

            candidatura.getReclutadores().add(reclutador);
            candidaturaRepository.save(candidatura);
            return true;
        }

        return false;
    }

    @Override
    @Transactional
    public boolean desasociarDeCandidatura(UUID reclutadorId, UUID candidaturaId) {
        Optional<Reclutador> reclutadorOpt = reclutadorRepository.findById(reclutadorId);
        Optional<Candidatura> candidaturaOpt = candidaturaRepository.findById(candidaturaId);

        if (reclutadorOpt.isPresent() && candidaturaOpt.isPresent()) {
            Reclutador reclutador = reclutadorOpt.get();
            Candidatura candidatura = candidaturaOpt.get();

            candidatura.getReclutadores().remove(reclutador);
            candidaturaRepository.save(candidatura);
            return true;
        }

        return false;
    }
}
