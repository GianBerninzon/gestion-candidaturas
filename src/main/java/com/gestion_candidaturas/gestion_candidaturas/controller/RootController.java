package com.gestion_candidaturas.gestion_candidaturas.controller;

import com.gestion_candidaturas.gestion_candidaturas.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/root")
public class RootController {

    @Autowired
    private UserService userService;

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ROOT')")
    public ResponseEntity<String> deleteUser(@PathVariable UUID id){
        if(userService.getUserById(id).isEmpty()){
            return ResponseEntity.notFound().build();
        }
        userService.deleteUserById(id);
        return ResponseEntity.ok("Usuario eliminado Correctamente");
    }
}
