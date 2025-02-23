package com.gestion_candidaturas.gestion_candidaturas.service;

import com.gestion_candidaturas.gestion_candidaturas.model.Candidatura;
import com.gestion_candidaturas.gestion_candidaturas.repository.CandidaturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CandidaturaServiceImpl implements CandidaturaService{

    @Autowired
    private CandidaturaRepository candidaturaRepository;

    @Override
    public List<Candidatura> getAllCandidaturasByUserId(Long userId) {
        return candidaturaRepository.findByUserId(userId);// Método personalizado en el repositorio
    }

    @Override
    public Optional<Candidatura> getCandidaturaByIdAndUserId(Long id, Long userId) {
        return candidaturaRepository.findByIdAndUserId(id, userId);
    }

    @Override
    public Candidatura saveCandidatura(Candidatura candidatura) {
        return candidaturaRepository.save(candidatura);
    }

    @Override
    public void deleteCandidaturaById(Long id) {
        candidaturaRepository.deleteById(id);
    }

    @Override
    public Optional<Candidatura> getCandidaturaById(Long id) {
        return candidaturaRepository.findById(id);
    }

    @Override
    public List<Candidatura> getAllCandidaturas() {
        return candidaturaRepository.findAll();// Para ADMIN/ROOT
    }
}
