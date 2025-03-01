package com.gestion_candidaturas.gestion_candidaturas.repository;

import com.gestion_candidaturas.gestion_candidaturas.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    // Buscar usuario por nombre de usuario
    Optional<User> findByUsername(String username);

    // Verificar si existe un usuario con ese nombre
    boolean existsByUsername(String username);

    // verificar si existe un usuario con ese email
    boolean existsByEmail(String email);
}
