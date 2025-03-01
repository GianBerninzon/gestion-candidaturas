package com.gestion_candidaturas.gestion_candidaturas.service;


import com.gestion_candidaturas.gestion_candidaturas.model.Pregunta;
import com.gestion_candidaturas.gestion_candidaturas.repository.PreguntaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


/**
 * Implementación de la interfaz PreguntaService que proporciona
 * la lógica de negocio para la gestión de preguntas de entrevista.
 */
@Service
public class PreguntaServiceImpl implements PreguntaService{


    private final PreguntaRepository preguntaRepository;

    @Autowired
    public PreguntaServiceImpl(PreguntaRepository preguntaRepository){
        this.preguntaRepository = preguntaRepository;
    }


    @Override
    @Transactional(readOnly = true)
    public List<Pregunta> findByCandidaturaId(UUID candidaturaId) {
        return preguntaRepository.findByCandidaturaId(candidaturaId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pregunta> findById(UUID id) {
        return preguntaRepository.findById(id);
    }

    @Override
    @Transactional
    public Pregunta save(Pregunta pregunta) {
        return preguntaRepository.save(pregunta);
    }

    @Override
    @Transactional
    public boolean deleteById(UUID id) {
        if (preguntaRepository.existsById(id)) {
            preguntaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public int countByCandidaturaId(UUID candidaturaId) {
        return preguntaRepository.countByCandidaturaId(candidaturaId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isOwner(UUID preguntaId, UUID userId) {
        Optional<Pregunta> pregunta = preguntaRepository.findById(preguntaId);
        return pregunta.isPresent() && pregunta.get().getUsuario().getId().equals(userId);
    }
}
