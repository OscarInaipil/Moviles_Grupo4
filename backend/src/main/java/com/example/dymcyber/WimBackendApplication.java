package com.example.dymcyber;

import com.example.dymcyber.model.CandidataEntity;
import com.example.dymcyber.repository.CandidataRepository;
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
}
