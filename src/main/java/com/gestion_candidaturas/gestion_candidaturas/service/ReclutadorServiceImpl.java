package com.gestion_candidaturas.gestion_candidaturas.service;

import com.gestion_candidaturas.gestion_candidaturas.model.Reclutador;
import com.gestion_candidaturas.gestion_candidaturas.repository.ReclutadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReclutadorServiceImpl implements ReclutadorService{

    @Autowired
    private ReclutadorRepository reclutadorRepository;


    @Override
    public List<Reclutador> getAllReclutadores() {
        return reclutadorRepository.findAll();
    }

    @Override
    public Optional<Reclutador> getReclutadorById(Long id) {
        return reclutadorRepository.findById(id);
    }

    @Override
    public Reclutador saveReclutador(Reclutador reclutador) {
        return reclutadorRepository.save(reclutador);
    }

    @Override
    public void deleteReclutadorById(Long id) {
        reclutadorRepository.deleteById(id);
    }
}
