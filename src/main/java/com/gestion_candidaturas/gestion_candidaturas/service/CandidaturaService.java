package com.gestion_candidaturas.gestion_candidaturas.service;

import com.gestion_candidaturas.gestion_candidaturas.model.Candidatura;

import java.util.List;
import java.util.Optional;

public interface CandidaturaService {
    List<Candidatura> getAllCandidaturasByUserId(Long userId);// Filtrar por usuario
    Optional<Candidatura> getCandidaturaByIdAndUserId(Long id, Long userId);//validar pertenencia
    Candidatura saveCandidatura(Candidatura candidatura);
    void deleteCandidaturaById(Long id);
    Optional<Candidatura> getCandidaturaById(Long id);
    List<Candidatura> getAllCandidaturas();
}
