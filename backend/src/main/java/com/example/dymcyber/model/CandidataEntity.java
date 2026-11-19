package com.example.dymcyber.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "candidatas")
public class CandidataEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String nombre;

	@Column(nullable = false)
	private Integer aniosExperiencia;

	@Column(nullable = false)
	private String perfilUrl;

	@Column(nullable = false)
	private String estado = "Pendiente";

	@Column(nullable = false)
	private Long fechaRegistro = System.currentTimeMillis();

	private String cvNombre;

	@Column(length = 500)
	private String cvUri;

	public CandidataEntity() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Integer getAniosExperiencia() {
		return aniosExperiencia;
	}

	public void setAniosExperiencia(Integer aniosExperiencia) {
		this.aniosExperiencia = aniosExperiencia;
	}

	public String getPerfilUrl() {
		return perfilUrl;
	}

	public void setPerfilUrl(String perfilUrl) {
		this.perfilUrl = perfilUrl;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Long getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(Long fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	public String getCvNombre() {
		return cvNombre;
	}

	public void setCvNombre(String cvNombre) {
		this.cvNombre = cvNombre;
	}

	public String getCvUri() {
		return cvUri;
	}

	public void setCvUri(String cvUri) {
		this.cvUri = cvUri;
	}
}
