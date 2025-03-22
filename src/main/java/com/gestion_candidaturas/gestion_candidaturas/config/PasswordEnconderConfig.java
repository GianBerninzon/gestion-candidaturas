package com.gestion_candidaturas.gestion_candidaturas.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuración de codificador de contraseñas.
 * Separada para evitar dependencia circular.
 */
@Configuration
public class PasswordEnconderConfig {

    /**
     * Proporciona un codificador de contraseñas para la aplicación.
     * Utiliza BCrypt para una encriptación segura.
     *
     * @return Codificador de contraseñas
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
