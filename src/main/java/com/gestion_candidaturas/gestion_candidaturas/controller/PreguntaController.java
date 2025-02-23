package com.gestion_candidaturas.gestion_candidaturas.controller;

import com.gestion_candidaturas.gestion_candidaturas.model.Pregunta;
import com.gestion_candidaturas.gestion_candidaturas.service.PreguntaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/preguntas")
public class PreguntaController {

    @Autowired
    private PreguntaService preguntaService;

    @GetMapping
    public ResponseEntity<List<Pregunta>> getAllPreguntas(){
        return ResponseEntity.ok(preguntaService.getAllPreguntas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pregunta> getPreguntaById(@PathVariable Long id){
        return preguntaService.getPreguntaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Pregunta> createPregunta(@RequestBody Pregunta pregunta){
        return ResponseEntity.ok(preguntaService.savePregunta(pregunta));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pregunta> updatePregunta(@PathVariable Long id, @RequestBody Pregunta pregunta){
        if(preguntaService.getPreguntaById(id).isEmpty()){
            return ResponseEntity.notFound().build();
        }
        pregunta.setId(id);
        return ResponseEntity.ok(preguntaService.savePregunta(pregunta));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePreguntaById(@PathVariable Long id){
        if(preguntaService.getPreguntaById(id).isEmpty()){
            return ResponseEntity.notFound().build();
        }
        preguntaService.deletePreguntaById(id);
        return ResponseEntity.ok().build();
    }
}
