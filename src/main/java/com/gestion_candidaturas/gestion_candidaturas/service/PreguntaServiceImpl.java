package com.gestion_candidaturas.gestion_candidaturas.service;

import com.gestion_candidaturas.gestion_candidaturas.model.Pregunta;
import com.gestion_candidaturas.gestion_candidaturas.repository.PreguntaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PreguntaServiceImpl implements PreguntaService{

    @Autowired
    private PreguntaRepository preguntaRepository;

    @Override
    public List<Pregunta> getAllPreguntas() {
        return preguntaRepository.findAll();
    }

    @Override
    public Optional<Pregunta> getPreguntaById(Long id) {
        return preguntaRepository.findById(id);
    }

    @Override
    public Pregunta savePregunta(Pregunta pregunta) {
        return preguntaRepository.save(pregunta);
    }

    @Override
    public void deletePreguntaById(Long id) {
        preguntaRepository.deleteById(id);
    }
}
