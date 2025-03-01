package com.gestion_candidaturas.gestion_candidaturas.security;

import com.gestion_candidaturas.gestion_candidaturas.model.Role;
import com.gestion_candidaturas.gestion_candidaturas.model.User;
import com.gestion_candidaturas.gestion_candidaturas.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


/**
 * Pruebas unitarias para el filtro de autenticación JWT.
 * Verifica el correcto procesamiento de tokens JWT en las solicitudes HTTP.
 */
public class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserService userService;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;
    private User testUser;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
        testUser = new User(UUID.randomUUID(), "testUser", "password", "test@example.com", Role.USER);

        // Limpiar el contexto de seguridad antes de cada prueba
        SecurityContextHolder.clearContext();
    }

    /**
     * Verifica que el filtro establezca la autenticación cuando el token es válido.
     */
    @Test
    public void shouldSetAuthenticationWhenValidToken() throws Exception {
        // Configurar token en la solicitud
        request.addHeader("Authorization", "Bearer valid_token");

        // Configurar comportamiento de los mocks
        when(jwtUtil.extractUsername(anyString())).thenReturn("testuser");
        when(userService.loadUserByUsername("testuser")).thenReturn(testUser);
        when(jwtUtil.validateToken(anyString(), any())).thenReturn(true);

        // Ejecutar el filtro
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verificar que se haya establecido la autenticación
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * Verifica que el filtro no establezca la autenticación cuando no hay token.
     */
    @Test
    public void shouldNotSetAuthenticationWhenNoToken() throws Exception {
        // No se agrega encabezado de autorizaciòn

        // Ejecutar el filtro
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verificar que no se haya establecido la autenticación
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * Verifica que el filtro no establezca la autenticación cuando el token es inválido.
     */
    @Test
    public void shouldNotSetAuthenticationWhenInvalidToken() throws Exception {
        // Configurar token en la solicitud
        request.addHeader("Authorization", "Bearer invalid_token");

        // Configurar comportamiento de los mocks
        when(jwtUtil.extractUsername(anyString())).thenReturn("testuser");
        when(userService.loadUserByUsername("testuser")).thenReturn(testUser);
        when(jwtUtil.validateToken(anyString(), any())).thenReturn(false);

        // Ejecutar el filtro
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verificar que no se haya establecido la auteticación
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * Verifica que el filtro maneje correctamente las excepciones.
     */
    @Test
    public void shouldHandleExceptionsGracefully() throws Exception {
        // Configurar token en la solicitud
        request.addHeader("Authorization", "Bearer invalid_token");

        // Configurar comportamiento de los mocks para lanzar excepción
        when(jwtUtil.extractUsername(anyString())).thenThrow(new RuntimeException("Test exception"));

        // Ejecutar el filtro (no debería lanzar excepción)
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verificar que no se haya extablecido la autenticación
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
