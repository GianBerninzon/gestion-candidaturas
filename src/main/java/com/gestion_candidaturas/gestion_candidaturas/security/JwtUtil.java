package com.gestion_candidaturas.gestion_candidaturas.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utilidad para generar y validar tokens JWT.
 *
 * @see RF-10: Autenticación mediante JWT
 * @see RNF-11: Seguridad mediante JWT
 */
@Component
public class JwtUtil {

    // Tiempo de validez del token en milisegundos (24 horas)
    private static final long TOKEN_VALIDITY = 24 * 60 * 60 * 1000;

    // Clave secreta para firmar los tokens
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    /**
     * Genera un token JWT para un usuario.
     *
     * @param userDetails Detalles del usuario para el que se generará el token
     * @return Token JWT generado
     */
    public String generateToken(UserDetails userDetails){
        Map<String, Object> claims = new HashMap<>();

        // Agregar información de roles en los claims
        claims.put("authorities", userDetails.getAuthorities());

        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Crea un token JWT con los claims y el subject especificados.
     *
     * @param claims Datos adicionales a incluir en el token
     * @param subject Identificador del usuario (generalmente username)
     * @return Token JWT creado
     */
    private String createToken(Map<String, Object> claims, String subject){
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + TOKEN_VALIDITY);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /**
     * Extrae el username del token JWT.
     *
     * @param token Token JWT
     * @return Username contenido en el token
     */
    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae la fecha de expiración del token JWT.
     *
     * @param token Token JWT
     * @return Fecha de expiración del token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrae un claim específico del token JWT.
     *
     * @param token Token JWT
     * @param claimsResolver Función para extraer un tipo específico de claim
     * @return El claim extraído
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrae todos los claims del token JWT.
     *
     * @param token Token JWT
     * @return Todos los claims contenidos en el token
     */
    private Claims extractAllClaims(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Verifica si el token ha expirado.
     *
     * @param token Token JWT
     * @return true si el token ha expirado, false en caso contrario
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }


    /**
     * Valida un token JWT para un usuario específico.
     *
     * @param token Token JWT a validar
     * @param userDetails Detalles del usuario para el que se validará el token
     * @return true si el token es válido para el usuario, false en caso contrario
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
