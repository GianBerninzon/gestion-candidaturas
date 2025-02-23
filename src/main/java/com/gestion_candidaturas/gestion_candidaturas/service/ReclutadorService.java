package com.gestion_candidaturas.gestion_candidaturas.service;

import com.gestion_candidaturas.gestion_candidaturas.model.Reclutador;

import java.util.List;
import java.util.Optional;

public interface ReclutadorService {
    List<Reclutador> getAllReclutadores();
    Optional<Reclutador> getReclutadorById(Long id);
    Reclutador saveReclutador(Reclutador reclutador);
    void deleteReclutadorById(Long id);
}
