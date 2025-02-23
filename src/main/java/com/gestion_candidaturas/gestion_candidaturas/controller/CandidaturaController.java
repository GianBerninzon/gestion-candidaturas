package com.gestion_candidaturas.gestion_candidaturas.controller;

import com.gestion_candidaturas.gestion_candidaturas.model.Candidatura;
import com.gestion_candidaturas.gestion_candidaturas.model.Role;
import com.gestion_candidaturas.gestion_candidaturas.model.User;
import com.gestion_candidaturas.gestion_candidaturas.service.CandidaturaService;
import com.gestion_candidaturas.gestion_candidaturas.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidaturas")
public class CandidaturaController {

    @Autowired
    private CandidaturaService candidaturaService;

    @Autowired
    private UserService userService;

    // Obtener todas las candidaturas del usuario actual (USER) o todas (ADMIN/ROOT)
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ROOT')")
    public ResponseEntity<List<Candidatura>> getAllCandidaturas(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByUsername(username);

        if(user.getRole() == Role.USER){
            return ResponseEntity.ok(candidaturaService.getAllCandidaturasByUserId(user.getId()));
        }else {
            return ResponseEntity.ok(candidaturaService.getAllCandidaturas());// Método para ADMIN/ROOT
        }
    }

    // Obtener una candidatura por ID (solo si pertenece al usuario o es ADMIN/ROOT)
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ROOT')")
    public ResponseEntity<Candidatura> getCandidaturaById(@PathVariable Long id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        if (role.equals("ROLE_USER")) {
            Long userId = ((User) userDetails).getId();
            return candidaturaService.getCandidaturaByIdAndUserId(id, userId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } else {
            return candidaturaService.getCandidaturaById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }
    }

    // Crear candidatura (asociada al usuario actual)
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ROOT')")
    public ResponseEntity<Candidatura> createCandidatura(@RequestBody Candidatura candidatura) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        candidatura.setUser((User) userDetails); // Asignar el usuario actual
        return ResponseEntity.ok(candidaturaService.saveCandidatura(candidatura));
    }

    // Actualizar candidatura (solo si pertenece al usuario o es ADMIN/ROOT)
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ROOT')")
    public ResponseEntity<Candidatura> updateCandidatura(@PathVariable Long id, @RequestBody Candidatura candidatura) {
        // Validar pertenencia o permisos
        return candidaturaService.getCandidaturaById(id)
                .map(existingCandidatura -> {
                    candidatura.setId(id);
                    return ResponseEntity.ok(candidaturaService.saveCandidatura(candidatura));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Eliminar candidatura (solo ADMIN/ROOT)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ROOT')")
    public ResponseEntity<Void> deleteCandidaturaById(@PathVariable Long id) {
        if (candidaturaService.getCandidaturaById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        candidaturaService.deleteCandidaturaById(id);
        return ResponseEntity.ok().build();
    }
}
