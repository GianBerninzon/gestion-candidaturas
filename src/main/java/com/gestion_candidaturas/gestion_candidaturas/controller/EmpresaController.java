package com.gestion_candidaturas.gestion_candidaturas.controller;

import com.gestion_candidaturas.gestion_candidaturas.model.Empresa;
import com.gestion_candidaturas.gestion_candidaturas.service.EmpresaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    @Autowired
    private EmpresaService empresaService;

    @GetMapping
    public ResponseEntity<List<Empresa>> getAllEmpresas(){
        return ResponseEntity.ok(empresaService.getAllEmpresas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Empresa> getEmpresaById(@PathVariable Long id){
        return empresaService.getEmpresaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Empresa> createEmpresa(@RequestBody Empresa empresa){
        return ResponseEntity.ok(empresaService.saveEmpresa(empresa));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Empresa> updateEmpresa(@PathVariable Long id, @RequestBody Empresa empresa){
        if(!empresaService.getEmpresaById(id).isPresent()){
            return ResponseEntity.notFound().build();
        }
        empresa.setId(id);

        return ResponseEntity.ok(empresaService.saveEmpresa(empresa));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmpresaById(@PathVariable Long id){
        if ( empresaService.getEmpresaById(id).isPresent()){
            return ResponseEntity.notFound().build();
        }
        empresaService.deleteEmpresaById(id);
        return ResponseEntity.ok().build();
    }
}
