package com.gestion_candidaturas.gestion_candidaturas;

import com.gestion_candidaturas.gestion_candidaturas.model.Role;
import com.gestion_candidaturas.gestion_candidaturas.model.User;
import com.gestion_candidaturas.gestion_candidaturas.repository.UserRepository;
import com.gestion_candidaturas.gestion_candidaturas.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void testLoadUserByUsename_UsuarioExiste(){
        //Configurar mock
        User user = new User();
        user.setUsername("test");
        user.setPassword("password");
        user.setRole(Role.USER);
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));

        //Ejecutar
        UserDetails userDetails = userService.loadUserByUsername("test");

        //Verificar
        assertEquals("test", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    public void testLoadUserByUsername_UsuarioNoexiste(){
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("unknown");
        });
    }
}
