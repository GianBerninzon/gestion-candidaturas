package com.gestion_candidaturas.gestion_candidaturas.repository;

import com.gestion_candidaturas.gestion_candidaturas.model.Candidatura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CandidaturaRepository extends JpaRepository<Candidatura, Long> {
    List<Candidatura> findByUserId(Long userId);//Obtener candidaturas por usuario
    Optional<Candidatura> findByIdAndUserId(Long id, Long userId); //Validar pertenencia
}
