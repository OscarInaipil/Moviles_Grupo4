package com.example.dymcyber.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "evaluaciones")
public class EvaluacionEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private Long candidataId;

	@Column(nullable = false)
	private String evaluadoraId;

	@Column(nullable = false)
	private Integer notaLiderazgo;

	@Column(nullable = false)
	private Integer notaTrayectoria;

	@Column(nullable = false)
	private Integer notaImpacto;

	@Column(nullable = false)
	private Integer notaInnovacion;

	@Column(nullable = false)
	private Double puntajePromedio;

	@Column(nullable = false, length = 1000)
	private String observaciones;

	@Column(nullable = false)
	private Long fechaEvaluacion = System.currentTimeMillis();

	public EvaluacionEntity() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCandidataId() {
		return candidataId;
	}

	public void setCandidataId(Long candidataId) {
		this.candidataId = candidataId;
	}

	public String getEvaluadoraId() {
		return evaluadoraId;
	}

	public void setEvaluadoraId(String evaluadoraId) {
		this.evaluadoraId = evaluadoraId;
	}

	public Integer getNotaLiderazgo() {
		return notaLiderazgo;
	}

	public void setNotaLiderazgo(Integer notaLiderazgo) {
		this.notaLiderazgo = notaLiderazgo;
	}

	public Integer getNotaTrayectoria() {
		return notaTrayectoria;
	}

	public void setNotaTrayectoria(Integer notaTrayectoria) {
		this.notaTrayectoria = notaTrayectoria;
	}

	public Integer getNotaImpacto() {
		return notaImpacto;
	}

	public void setNotaImpacto(Integer notaImpacto) {
		this.notaImpacto = notaImpacto;
	}

	public Integer getNotaInnovacion() {
		return notaInnovacion;
	}

	public void setNotaInnovacion(Integer notaInnovacion) {
		this.notaInnovacion = notaInnovacion;
	}

	public Double getPuntajePromedio() {
		return puntajePromedio;
	}

	public void setPuntajePromedio(Double puntajePromedio) {
		this.puntajePromedio = puntajePromedio;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public Long getFechaEvaluacion() {
		return fechaEvaluacion;
	}

	public void setFechaEvaluacion(Long fechaEvaluacion) {
		this.fechaEvaluacion = fechaEvaluacion;
	}
}
