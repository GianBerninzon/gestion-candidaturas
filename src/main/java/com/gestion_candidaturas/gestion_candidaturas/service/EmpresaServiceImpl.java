package com.gestion_candidaturas.gestion_candidaturas.service;

import com.gestion_candidaturas.gestion_candidaturas.model.Empresa;
import com.gestion_candidaturas.gestion_candidaturas.repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmpresaServiceImpl implements EmpresaService{

    @Autowired
    private EmpresaRepository empresaRepository;

    @Override
    public List<Empresa> getAllEmpresas() {
        return empresaRepository.findAll();
    }

    @Override
    public Optional<Empresa> getEmpresaById(Long id) {
        return empresaRepository.findById(id);
    }

    @Override
    public Empresa saveEmpresa(Empresa empresa) {
        return empresaRepository.save(empresa);
    }

    @Override
    public void deleteEmpresaById(Long id) {
        empresaRepository.deleteById(id);

    }
}
