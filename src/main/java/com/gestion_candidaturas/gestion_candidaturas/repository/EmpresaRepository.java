package com.gestion_candidaturas.gestion_candidaturas.repository;

import com.gestion_candidaturas.gestion_candidaturas.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
}
