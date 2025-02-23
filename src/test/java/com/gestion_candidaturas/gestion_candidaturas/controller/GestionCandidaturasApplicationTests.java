package com.gestion_candidaturas.gestion_candidaturas.controller;

import com.gestion_candidaturas.gestion_candidaturas.model.Candidatura;
import com.gestion_candidaturas.gestion_candidaturas.model.Role;
import com.gestion_candidaturas.gestion_candidaturas.model.User;
import com.gestion_candidaturas.gestion_candidaturas.service.CandidaturaService;
import com.gestion_candidaturas.gestion_candidaturas.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class GestionCandidaturasApplicationTests {

	@MockitoBean
	private CandidaturaService candidaturaService;

	@MockitoBean
	private UserService userService;

	@Autowired
	private CandidaturaController candidaturaController;

	@Test
	@WithMockUser(username = "testuser", roles = "USER")
	public void testCreateCandidatura(){
		User user = new User();
		user.setId(1L);
		user.setUsername("testuser");
		user.setRole(Role.USER);
		// Simula la creación de una nueva candidatura
		Candidatura candidatura = new Candidatura();
		when(candidaturaService.saveCandidatura(candidatura)).thenReturn(candidatura);

		ResponseEntity<Candidatura> response = candidaturaController.createCandidatura(candidatura);
		assertEquals(200, response.getStatusCodeValue());
		assertEquals(candidatura, response.getBody());
	}

	@Test
	@WithMockUser(username = "testuser", roles = "USER")
	public void testGetAllCandidaturas_Usuario() {
		User user = new User();
		user.setId(1L);
		user.setUsername("testuser");
		user.setRole(Role.USER);

		when(userService.getUserByUsername("testuser")).thenReturn(user); // Mockear getCurrentUser()
		when(candidaturaService.getAllCandidaturasByUserId(1L)).thenReturn(List.of(new Candidatura()));

		ResponseEntity<List<Candidatura>> response = candidaturaController.getAllCandidaturas();
		assertEquals(200, response.getStatusCodeValue());
		assertFalse(response.getBody().isEmpty());
	}

	@Test
	public void testGetCandidaturaById_Existente(){
		//Simula una candidatura existente
		Candidatura candidatura = new Candidatura();
		when(candidaturaService.getCandidaturaById(1L)).thenReturn(Optional.of(candidatura));

		ResponseEntity<Candidatura> response = candidaturaController.getCandidaturaById(1L);
		assertEquals(200, response.getStatusCodeValue());
		assertEquals(candidatura, response.getBody());
	}

	@Test
	public void testGetCandidaturaById_NoExistente(){
		// Simula una candidatura no existente
		when(candidaturaService.getCandidaturaById(1L)).thenReturn(Optional.empty());

		ResponseEntity<Candidatura> response = candidaturaController.getCandidaturaById(1L);
		assertEquals(404, response.getStatusCodeValue());
	}

	@Test
	public void testUpdateCandidatura_Existente() {
		// Simula la actualización de una candidatura existente
		Candidatura candidatura = new Candidatura();
		when(candidaturaService.getCandidaturaById(1L)).thenReturn(Optional.of(candidatura));
		when(candidaturaService.saveCandidatura(candidatura)).thenReturn(candidatura);

		ResponseEntity<Candidatura> response = candidaturaController.updateCandidatura(1L, candidatura);
		assertEquals(200, response.getStatusCodeValue());
		assertEquals(candidatura, response.getBody());
	}

	@Test
	public void testUpdateCandidatura_NoExistente(){
		// Simula la actualización de una candidatura no existente
		Candidatura candidatura = new Candidatura();
		when(candidaturaService.getCandidaturaById(1L)).thenReturn(Optional.empty());

		ResponseEntity<Candidatura> response = candidaturaController.updateCandidatura(1L, candidatura);
		assertEquals(404, response.getStatusCodeValue());
	}

	@Test
	public void testDeleteCandidaturaById_Existente(){
		// Simula la eliminación de una candidatura existente
		when(candidaturaService.getCandidaturaById(1L)).thenReturn(Optional.of(new Candidatura()));

		ResponseEntity<Void> response = candidaturaController.deleteCandidaturaById(1L);
		assertEquals(200, response.getStatusCodeValue());
	}

	@Test
	public void testDeleteCandidaturaById_NoExistente() {
		when(candidaturaService.getCandidaturaById(1L)).thenReturn(Optional.empty());
		ResponseEntity<Void> response = candidaturaController.deleteCandidaturaById(1L);
		assertEquals(404, response.getStatusCodeValue());
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	public void testGetAllCandidaturas_Admin() {
		when(candidaturaService.getAllCandidaturas()).thenReturn(List.of(new Candidatura()));// Mockear getAllCandidaturas()
		ResponseEntity<List<Candidatura>> response = candidaturaController.getAllCandidaturas();
		assertEquals(200, response.getStatusCodeValue());
		assertFalse(response.getBody().isEmpty());
	}

}
