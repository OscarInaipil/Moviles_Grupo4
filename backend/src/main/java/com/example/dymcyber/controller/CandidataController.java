package com.example.dymcyber.controller;

import com.example.dymcyber.model.CandidataEntity;
import com.example.dymcyber.repository.CandidataRepository;
import com.example.dymcyber.repository.EvaluacionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/candidatas")
@CrossOrigin(origins = "*")
public class CandidataController {

	private final CandidataRepository candidataRepository;
	private final EvaluacionRepository evaluacionRepository;

	public CandidataController(CandidataRepository candidataRepository, EvaluacionRepository evaluacionRepository) {
		this.candidataRepository = candidataRepository;
		this.evaluacionRepository = evaluacionRepository;
	}

	@GetMapping
	public List<CandidataEntity> listarTodas() {
		return candidataRepository.findAllByOrderByIdDesc();
	}

	@GetMapping("/{id}")
	public ResponseEntity<CandidataEntity> obtenerPorId(@PathVariable Long id) {
		Optional<CandidataEntity> candidata = candidataRepository.findById(id);
		return candidata.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<CandidataEntity> crear(@RequestBody CandidataEntity candidata) {
		if (candidata.getNombre() == null || candidata.getNombre().trim().length() < 3) {
			return ResponseEntity.badRequest().build();
		}
		if (candidata.getAniosExperiencia() == null || candidata.getAniosExperiencia() < 0) {
			return ResponseEntity.badRequest().build();
		}
		if (candidata.getPerfilUrl() == null || candidata.getPerfilUrl().trim().isEmpty()) {
			return ResponseEntity.badRequest().build();
		}
		if (candidata.getEstado() == null || candidata.getEstado().trim().isEmpty()) {
			candidata.setEstado("Pendiente");
		}
		if (candidata.getFechaRegistro() == null) {
			candidata.setFechaRegistro(System.currentTimeMillis());
		}
		candidata.setId(null);
		CandidataEntity guardada = candidataRepository.save(candidata);
		return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
	}

	@PutMapping("/{id}")
	public ResponseEntity<CandidataEntity> actualizar(@PathVariable Long id, @RequestBody CandidataEntity datos) {
		Optional<CandidataEntity> existente = candidataRepository.findById(id);
		if (existente.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		CandidataEntity candidata = existente.get();
		if (datos.getNombre() != null && !datos.getNombre().trim().isEmpty()) {
			candidata.setNombre(datos.getNombre().trim());
		}
		if (datos.getAniosExperiencia() != null && datos.getAniosExperiencia() >= 0) {
			candidata.setAniosExperiencia(datos.getAniosExperiencia());
		}
		if (datos.getPerfilUrl() != null && !datos.getPerfilUrl().trim().isEmpty()) {
			candidata.setPerfilUrl(datos.getPerfilUrl().trim());
		}
		if (datos.getEstado() != null && !datos.getEstado().trim().isEmpty()) {
			candidata.setEstado(datos.getEstado());
		}
		if (datos.getCvNombre() != null) {
			candidata.setCvNombre(datos.getCvNombre());
		}
		if (datos.getCvUri() != null) {
			candidata.setCvUri(datos.getCvUri());
		}
		CandidataEntity actualizada = candidataRepository.save(candidata);
		return ResponseEntity.ok(actualizada);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		if (!candidataRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		evaluacionRepository.findByCandidataId(id).ifPresent(evaluacionRepository::delete);
		candidataRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}
