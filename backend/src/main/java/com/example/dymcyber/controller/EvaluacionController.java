package com.example.dymcyber.controller;

import com.example.dymcyber.model.CandidataEntity;
import com.example.dymcyber.model.EvaluacionEntity;
import com.example.dymcyber.repository.CandidataRepository;
import com.example.dymcyber.repository.EvaluacionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/evaluaciones")
@CrossOrigin(origins = "*")
public class EvaluacionController {

	private final EvaluacionRepository evaluacionRepository;
	private final CandidataRepository candidataRepository;

	public EvaluacionController(EvaluacionRepository evaluacionRepository, CandidataRepository candidataRepository) {
		this.evaluacionRepository = evaluacionRepository;
		this.candidataRepository = candidataRepository;
	}

	@GetMapping
	public List<EvaluacionEntity> listarTodas() {
		return evaluacionRepository.findAllByOrderByPuntajePromedioDesc();
	}

	@GetMapping("/candidata/{candidataId}")
	public ResponseEntity<EvaluacionEntity> obtenerPorCandidataId(@PathVariable Long candidataId) {
		Optional<EvaluacionEntity> evaluacion = evaluacionRepository.findByCandidataId(candidataId);
		return evaluacion.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<?> guardarEvaluacion(@RequestBody EvaluacionEntity evaluacion) {
		if (evaluacion.getCandidataId() == null) {
			return ResponseEntity.badRequest().body("El ID de la candidata es obligatorio");
		}
		Optional<CandidataEntity> candidataOpt = candidataRepository.findById(evaluacion.getCandidataId());
		if (candidataOpt.isEmpty()) {
			return ResponseEntity.badRequest().body("La candidata indicada no existe");
		}
		if (evaluacionRepository.existsByCandidataId(evaluacion.getCandidataId())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Esta candidata ya cuenta con una evaluacion registrada");
		}
		if (evaluacion.getEvaluadoraId() == null || evaluacion.getEvaluadoraId().trim().length() < 3) {
			return ResponseEntity.badRequest().body("El identificador de la evaluadora es invalido");
		}
		if (evaluacion.getObservaciones() == null || evaluacion.getObservaciones().trim().length() < 10) {
			return ResponseEntity.badRequest().body("La justificacion debe tener al menos 10 caracteres");
		}
		if (evaluacion.getFechaEvaluacion() == null) {
			evaluacion.setFechaEvaluacion(System.currentTimeMillis());
		}

		double suma = evaluacion.getNotaLiderazgo() + evaluacion.getNotaTrayectoria() + evaluacion.getNotaImpacto() + evaluacion.getNotaInnovacion();
		double promedio = Math.round((suma / 4.0) * 10.0) / 10.0;
		evaluacion.setPuntajePromedio(promedio);

		EvaluacionEntity guardada = evaluacionRepository.save(evaluacion);

		CandidataEntity candidata = candidataOpt.get();
		candidata.setEstado("Evaluada");
		candidataRepository.save(candidata);

		return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
	}
}
