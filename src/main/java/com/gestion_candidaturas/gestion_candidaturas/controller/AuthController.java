package com.gestion_candidaturas.gestion_candidaturas.controller;

import com.gestion_candidaturas.gestion_candidaturas.dto.AuthRequest;
import com.gestion_candidaturas.gestion_candidaturas.dto.RegisterRequest;
import com.gestion_candidaturas.gestion_candidaturas.model.User;
import com.gestion_candidaturas.gestion_candidaturas.security.JwtUtil;
import com.gestion_candidaturas.gestion_candidaturas.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para gestionar la autenticación de usuarios.
 *
 * @see RF-10: Registro y autenticación de usuarios
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     * Constructor para inyección de dependencias.
     *
     * @param authenticationManager Gestor de autenticación de Spring Security
     * @param userService Servicio para operaciones con usuarios
     * @param jwtUtil Utilidad para operaciones con JWT
     */
    public AuthController(AuthenticationManager authenticationManager,
                          UserService userService,
                          JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Endpoint para iniciar sesión y obtener un token JWT.
     *
     * @param authRequest Credenciales de autenticación (username y password)
     * @return Token JWT e información básica del usuario si la autenticación es exitosa
     *
     * @see RF-10: Autenticación mediante credenciales
     * @see RNF-11: Uso de JWT para seguridad en la comunicación
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest authRequest){
        try {
            // Autenticar al usuario
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );

            // Establecer la autenticación en el contexto de seguridad
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Obtener el usuario autenticado
            User user = userService.getUserByUsername(authRequest.getUsername());

            // Generar token JWT
            String jwt = jwtUtil.generateToken(user);

            // Contruir respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("id", user.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("role", user.getRole().name());

            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales inválidas"));
                    //.body("error: Credenciales inválidas");
        }
    }

    /**
     * Endpoint para registrar un nuevo usuario.
     *
     * @param registerRequest Datos de registro (username, email, password, rol opcional)
     * @return Mensaje de confirmación si el registro es exitoso
     *
     * @see RF-10: Registro de usuarios
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        // Verificar si el nombre de usuario ya existe
        if (userService.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El nombre de usuario ya está en uso"));
                    //.body("Error: El nombre de usuario ya está en uso");
        }

        // Verificar si el email ya existe
        if (userService.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "el email ya está en uso"));
                    //.body("Error: El email ya está en uso");
        }

        // Registrar el nuevo usuario
        try {
            User newUser = userService.register(
                    registerRequest.getUsername(),
                    registerRequest.getEmail(),
                    registerRequest.getPassword(),
                    registerRequest.getRol()
            );

            // Construir respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Usuario registrado correctamente");
            response.put("username", newUser.getUsername());
            response.put("role", newUser.getRole().name());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar el usuario: " + e.getMessage());
        }
    }

    /**
     * Endpoint para obtener información del usuario autenticado.
     *
     * @return Datos básicos del usuario actual
     *
     * @see RF-10: Obtención de información del usuario autenticado
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurretUser() {
       try {
           // Obtener el usuario actual
           User currentUser = userService.getCurrentUser();

           // Construir respuesta
           Map<String, Object> response = new HashMap<>();
           response.put("id", currentUser.getId());
           response.put("username", currentUser.getUsername());
           response.put("email", currentUser.getEmail());
           response.put("rol", currentUser.getRole().name());

           return ResponseEntity.ok(response);
       }catch (UsernameNotFoundException e){
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Usuario no autenticado");
       }
    }

    /**
     * Endpoint para cerrar sesión.
     * Invalida el token JWT limpiando el contexto de seguridad.
     *
     * @return Mensaje de confirmación
     *
     * @see RF-10: Cierre de sesión seguro
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(){
        // Limpiar el contexto de seguridad
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok(Map.of("message", "Sesión cerrada correctamente"));
    }
}
