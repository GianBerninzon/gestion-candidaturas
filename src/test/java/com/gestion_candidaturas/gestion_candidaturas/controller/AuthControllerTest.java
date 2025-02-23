package com.gestion_candidaturas.gestion_candidaturas.controller;

import com.gestion_candidaturas.gestion_candidaturas.model.Role;
import com.gestion_candidaturas.gestion_candidaturas.model.User;
import com.gestion_candidaturas.gestion_candidaturas.security.AuthRequest;
import com.gestion_candidaturas.gestion_candidaturas.security.JwtUtil;
import com.gestion_candidaturas.gestion_candidaturas.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;


@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserService userService;

    @Autowired
    private AuthController authController;

    @Autowired
    private RootController rootController;

    @Test
    public void testRegister_UsuarioNuevo(){
        AuthRequest request = new AuthRequest("test", "password");
        when(userService.findByUsername("test")).thenReturn(Optional.empty());

        ResponseEntity<String> response = authController.register(request);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Usuario registrado Correctamente", response.getBody());
    }

    @Test
    public void testRegister_UsuarioExistente(){
        AuthRequest request = new AuthRequest("test", "password");
        when(userService.findByUsername("test")).thenReturn(Optional.of(new User()));

        ResponseEntity<String> response = authController.register(request);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Usuario ya existe", response.getBody());
    }

    @Test
    public void testLogin_CredencialesValidas() {
        AuthRequest request = new AuthRequest("test", "password");
        when(jwtUtil.generateToken("test")).thenReturn("token_jwt_simulado");

        String token = authController.login(request);
        assertEquals("token_jwt_simulado", token);
    }

    @Test
    public void testLogin_CredencialesInvalidas() {
        AuthRequest request = new AuthRequest("test", "wrong_password");
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Credenciales inválidas"));

        assertThrows(BadCredentialsException.class, () -> authController.login(request));
    }

    @Test
    @WithMockUser(roles = "ROOT")//Simula un usuario con el rol ROOT
    public void testDeleteUser_Existe(){
        //Crear un usuario simulado con ID 1L
        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setPassword("password");
        user.setRole(Role.ROOT);

        //Simular el metodo getUserById para devolver el usuario simulado
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        //Llamar al metodo deleteUser del RooTController
        ResponseEntity<String> response = rootController.deleteUser(1L);

        //Verificar que la respuesta es 200 OK
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Usuario eliminado Correctamente", response.getBody());
    }

    @Test
    @WithMockUser(roles = "ROOT")
    public void testDeleteUser_NoExiste() {
        when(userService.getUserById(1L)).thenReturn(Optional.empty());
        ResponseEntity<String> response = rootController.deleteUser(1L);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    @WithMockUser(roles = "USER") // Usuario sin permisos
    public void testDeleteUser_NoAutorizado() {
        //Simula que el usuario existe para evitar el 404
        when(userService.getUserById(1L)).thenReturn(Optional.of(new User()));

        //Verifica que se lanza AccessDeniedException
        assertThrows(AccessDeniedException.class,() -> {
            rootController.deleteUser(1L);
        });
    }

}
