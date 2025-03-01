package com.gestion_candidaturas.gestion_candidaturas.config;

import com.gestion_candidaturas.gestion_candidaturas.security.JwtAuthenticationFilter;
import com.gestion_candidaturas.gestion_candidaturas.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Configuración de seguridad para la aplicación.
 * Define las reglas de acceso, autenticación y autorización.
 *
 * @see RF-10: Autenticación de usuarios mediante JWT
 * @see RF-11: Control de acceso basado en roles
 * @see RNF-11: Autenticación con JWT
 * @see RNF-12: Validación de permisos en operaciones CRUD
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final UserService userService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Constructor para inyección de dependencias.
     *
     * @param userService Servicio de gestión de usuarios
     * @param jwtAuthenticationFilter Filtro de autenticación JWT
     */
    public SecurityConfig(UserService userService, JwtAuthenticationFilter jwtAuthenticationFilter){
        this.userService = userService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Configura la cadena de filtros de seguridad.
     * Define las reglas de acceso, la política de sesiones y los filtros.
     *
     * @param http Configurador de seguridad HTTP
     * @return La cadena de filtros de seguridad configurada
     * @throws Exception Si ocurre un error en la configuración
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http
                // Desactivar CSRF ya que usamos JWT
                .csrf(csrf -> csrf.disable())
                // Habilitar CORD
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                //Configurar manejo de sesiones
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Configurar reglas de autorización
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos (no requieren sutenticación)
                        .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
                        // Todos los demas endpoints requieren autenticación
                        .anyRequest().authenticated())
                // Agregar filtro de JWT antes del filtro de autenticación de usuario/contraseña
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Configura el origen CORS para permitir solicitudes desde el frontend.
     *
     * @return Configuración CORS
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // Origen del frontend
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return  source;
    }

    /**
     * Configura el proveedor de autenticación.
     *
     * @return Proveedor de autenticación configurado con el servicio de usuarios y el codificador de contraseñas
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Proporciona un gestor de autenticación para ser utilizado en el controlador de autenticación.
     *
     * @param config Configuración de autenticación
     * @return Gestor de autenticación
     * @throws Exception Si ocurre un error al obtener el gestor de autenticación
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception{
        return authConfig.getAuthenticationManager();
    }

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
