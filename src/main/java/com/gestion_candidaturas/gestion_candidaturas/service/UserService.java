package com.gestion_candidaturas.gestion_candidaturas.service;

import com.gestion_candidaturas.gestion_candidaturas.model.Role;
import com.gestion_candidaturas.gestion_candidaturas.model.User;
import com.gestion_candidaturas.gestion_candidaturas.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;


/**
 * Servicio que gestiona operaciones relacionadas con usuarios del sistema.
 * Implementa UserDetailsService para integrarse con Spring Security.
 *
 * @see RF-10: Permite registro y autenticación de usuarios
 * @see RF-11: Control de acceso basado en roles
 */
@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Carga los detalles de un usuario por su nombre de usuario.
     * Este método es requerido por Spring Security para el proceso de autenticación.
     *
     * @param username Nombre de usuario a buscar
     * @return UserDetails con la información necesaria para autenticación
     * @throws UsernameNotFoundException si el usuario no existe
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //Buscar usuario en la BD
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // Retornar las autoridades del usuario directamente
        return user;
    }

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param username Nombre de usuario a buscar
     * @return Optional con el usuario si existe, Optional vacío si no
     */
    public Optional<User> findByUsername(String username){
        return userRepository.findByUsername(username);
    }

    /**
     * Guarda un usuario en la base de datos, encriptando su contraseña previamente.
     *
     * @param user Usuario a guardar con contraseña sin encriptar
     * @return Usuario guardado con contraseña encriptada
     */
    public User save(User user){
        // Encriptar contraseña antes de guardar
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Busca un usuario por su ID.
     *
     * @param id ID del usuario a buscar
     * @return Optional con el usuario si existe, Optional vacío si no
     */
    public Optional<User> getUserById(UUID id){
        return userRepository.findById(id);
    }

    /**
     * Elimina un usuario por su ID.
     *
     * @param id ID del usuario a eliminar
     */
    public void deleteUserById(UUID id){
        userRepository.deleteById(id);
    }

    /**
     * Obtiene el usuario actualmente autenticado en el sistema.
     *
     * @return Usuario autenticado
     * @throws UsernameNotFoundException si el usuario no existe
     */
    public User getCurrentUser(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }

    /**
     * Busca un usuario por su nombre de usuario y lanza una excepción si no existe.
     *
     * @param username Nombre de usuario a buscar
     * @return Usuario encontrado
     * @throws UsernameNotFoundException si el usuario no existe
     */
    public User getUserByUsername(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }

    /**
     * Verifica si existe un usuario con el nombre de usuario especificado.
     *
     * @param username Nombre de usuario a verificar
     * @return true si existe un usuario con ese nombre, false en caso contrario
     */
    public boolean existsByUsername(String username){
        return userRepository.existsByUsername(username);
    }

    /**
     * Verifica si existe un usuario con el correo electrónico especificado.
     * @param email Correo electrónico a verificar
     * @return true si existe un usuario con ese email, false en caso contrario
     */
    public boolean existsByEmail(String email){
        return userRepository.existsByEmail(email);
    }

    /**
     * Registra un nuevo usuario en el sistema con los datos proporcionados.
     * La contraseña se encripta antes de almacenarse en la base de datos.
     *
     * @param username Nombre de usuario
     * @param email Correo electrónico
     * @param password Contraseña (sin encriptar)
     * @param rolName Nombre del rol (USER, ADMIN, ROOT)
     * @return El usuario creado
     */
    public User register(String username, String email, String password, String rolName){
        // Crear nueva instancia de usuario
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(password)); // Encriptar contraseña

        //Convertir el nombre del rol a enum Role
        Role role;
        try {
            role = Role.valueOf(rolName.toUpperCase());
        }catch (IllegalArgumentException e){
            // Si el ril no existe, asignar USER por defecto
            role = Role.USER;
        }
        newUser.setRole(role);

        // Guardar en la base de datos
        return userRepository.save(newUser);
    }

}
