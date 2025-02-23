package com.gestion_candidaturas.gestion_candidaturas.service;


import com.gestion_candidaturas.gestion_candidaturas.model.Empresa;

import java.util.List;
import java.util.Optional;

public interface EmpresaService {
    List<Empresa> getAllEmpresas();
    Optional<Empresa> getEmpresaById(Long id);
    Empresa saveEmpresa(Empresa empresa);
    void deleteEmpresaById(Long id);
}
