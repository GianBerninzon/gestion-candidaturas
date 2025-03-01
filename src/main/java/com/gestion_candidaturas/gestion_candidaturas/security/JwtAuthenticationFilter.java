package com.gestion_candidaturas.gestion_candidaturas.security;

import com.gestion_candidaturas.gestion_candidaturas.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    /**
     * Constructor para inyección de dependencias.
     *
     * @param jwtUtil Utilidad para manejo de tokens JWT
     * @param userService Servicio para operaciones con usuarios
     */
    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserService userService){
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    /**
     * Método principal del filtro que se ejecuta una vez por cada solicitud.
     * Extrae y valida el token JWT del encabezado de autorización.
     * Si el token es válido, autentica al usuario en el contexto de seguridad.
     *
     * @param request Solicitud HTTP
     * @param response Respuesta HTTP
     * @param filterChain Cadena de filtros para continuar el procesamiento
     * @throws ServletException Si ocurre un error en el servlet
     * @throws IOException Si ocurre un error de E/S
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            // Obtener el encabezado de autorización
            String authorizationHeader = request.getHeader("Authorization");

            // Verificar si el encabezado está presente y tiene formato adecuado
            if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
                // Extraer el token (quitar el prefijo "Bearer ")
                String jwt = authorizationHeader.substring(7);

                // Extraer el nombre de usuario del token
                String username= jwtUtil.extractUsername(jwt);

                // Verificar si el nombre de usuario válido y no hay autenticación previa
                if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Cargar detalles del usuario
                    UserDetails userDetails = userService.loadUserByUsername(username);

                    // Validar el token
                    if(jwtUtil.validateToken(jwt, userDetails)){
                        // Crear token de autenticación
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails, null, userDetails.getAuthorities()
                                );

                        // Establecer detalles de la solicitud
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        // Establecer la autenticación en el contexto de seguridad
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        }catch (Exception e){
            // Log del error (en un sistema real, se usaría un logger como SLF4J)
            System.err.println("No se pudo establecer la autenticación: " + e.getMessage());
        }

        //Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}
