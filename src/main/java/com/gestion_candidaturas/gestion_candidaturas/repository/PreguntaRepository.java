package com.gestion_candidaturas.gestion_candidaturas.repository;

import com.gestion_candidaturas.gestion_candidaturas.model.Pregunta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreguntaRepository extends JpaRepository<Pregunta, Long> {
}
