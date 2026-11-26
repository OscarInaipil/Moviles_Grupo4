package com.example.dymcyber;

import com.example.dymcyber.model.CandidataEntity;
import com.example.dymcyber.model.UsuarioEntity;
import com.example.dymcyber.repository.CandidataRepository;
import com.example.dymcyber.repository.UsuarioRepository;
import com.example.dymcyber.util.Encriptador;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class WimBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(WimBackendApplication.class, args);
	}

	@Bean
	CommandLineRunner initDatos(CandidataRepository candidataRepository) {
		return args -> {
			if (candidataRepository.count() == 0) {
				CandidataEntity c1 = new CandidataEntity();
				c1.setNombre("Elena Torres Muñoz");
				c1.setAniosExperiencia(8);
				c1.setPerfilUrl("https://linkedin.com/in/elena-torres-wim");
				c1.setEstado("Pendiente");

				CandidataEntity c2 = new CandidataEntity();
				c2.setNombre("Valentina Vega Rojas");
				c2.setAniosExperiencia(5);
				c2.setPerfilUrl("https://linkedin.com/in/valentina-vega-wim");
				c2.setEstado("Pendiente");

				CandidataEntity c3 = new CandidataEntity();
				c3.setNombre("Patricia Morales Silva");
				c3.setAniosExperiencia(12);
				c3.setPerfilUrl("https://linkedin.com/in/patricia-morales-wim");
				c3.setEstado("Pendiente");

				candidataRepository.saveAll(List.of(c1, c2, c3));
			}
		};
	}

	@Bean
	CommandLineRunner initUsuarios(UsuarioRepository usuarioRepository) {
		return args -> {
			if (usuarioRepository.count() == 0) {
				UsuarioEntity u1 = new UsuarioEntity();
				u1.setNombre("Camila Rojas");
				u1.setCorreo("evaluadora1@wim100.cl");
				u1.setContrasena(Encriptador.sha256("wim2026"));
				u1.setCodigoEvaluadora("EVA-01");

				UsuarioEntity u2 = new UsuarioEntity();
				u2.setNombre("Daniela Soto");
				u2.setCorreo("evaluadora2@wim100.cl");
				u2.setContrasena(Encriptador.sha256("wim2026"));
				u2.setCodigoEvaluadora("EVA-02");

				usuarioRepository.saveAll(List.of(u1, u2));
			}
		};
	}
}
