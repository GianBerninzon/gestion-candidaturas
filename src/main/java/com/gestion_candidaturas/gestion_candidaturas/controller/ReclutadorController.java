package com.gestion_candidaturas.gestion_candidaturas.controller;


import com.gestion_candidaturas.gestion_candidaturas.model.Reclutador;
import com.gestion_candidaturas.gestion_candidaturas.service.ReclutadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reclutador")
public class ReclutadorController {

    @Autowired
    private ReclutadorService reclutadorService;

    @GetMapping
    public ResponseEntity<List<Reclutador>> getAllReclutadores(){
        return ResponseEntity.ok(reclutadorService.getAllReclutadores());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reclutador> getReclutadorById(@PathVariable Long id){
        return reclutadorService.getReclutadorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Reclutador> createReclutador(@RequestBody Reclutador reclutador){
        return ResponseEntity.ok(reclutadorService.saveReclutador(reclutador));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reclutador> updateReclutador(@PathVariable Long id, @RequestBody Reclutador reclutador){
        if(reclutadorService.getReclutadorById(id).isEmpty()){
            return ResponseEntity.notFound().build();
        }
        reclutador.setId(id);
        return ResponseEntity.ok(reclutadorService.saveReclutador(reclutador));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReclutadorById(@PathVariable Long id){
        if(reclutadorService.getReclutadorById(id).isEmpty()){
            return ResponseEntity.notFound().build();
        }
        reclutadorService.deleteReclutadorById(id);
        return ResponseEntity.ok().build();
    }
}
