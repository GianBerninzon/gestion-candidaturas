package com.gestion_candidaturas.gestion_candidaturas.controller;

import com.gestion_candidaturas.gestion_candidaturas.model.Role;
import com.gestion_candidaturas.gestion_candidaturas.model.User;
import com.gestion_candidaturas.gestion_candidaturas.security.AuthRequest;
import com.gestion_candidaturas.gestion_candidaturas.security.JwtUtil;
import com.gestion_candidaturas.gestion_candidaturas.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public String login(@RequestBody AuthRequest authRequest){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        return jwtUtil.generateToken(authRequest.getUsername());
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest authRequest){
        if(userService.findByUsername(authRequest.getUsername()).isPresent()){
            return ResponseEntity.badRequest().body("Usuario ya existe");
        }

        User user = new User();
        user.setUsername(authRequest.getUsername());
        user.setPassword(authRequest.getPassword());
        user.setRole(Role.USER);

        userService.save(user);
        return ResponseEntity.ok("Usuario registrado Correctamente");
    }
}
