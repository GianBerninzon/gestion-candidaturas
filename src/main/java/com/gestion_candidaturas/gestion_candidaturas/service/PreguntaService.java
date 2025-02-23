package com.gestion_candidaturas.gestion_candidaturas.service;

import com.gestion_candidaturas.gestion_candidaturas.model.Pregunta;

import java.util.List;
import java.util.Optional;

public interface PreguntaService {
    List<Pregunta> getAllPreguntas();
    Optional<Pregunta> getPreguntaById(Long id);
    Pregunta savePregunta(Pregunta pregunta);
    void deletePreguntaById(Long id);
}
