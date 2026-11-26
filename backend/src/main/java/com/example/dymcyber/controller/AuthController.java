package com.example.dymcyber.controller;

import com.example.dymcyber.model.UsuarioEntity;
import com.example.dymcyber.repository.UsuarioRepository;
import com.example.dymcyber.util.Encriptador;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

	private final UsuarioRepository usuarioRepository;

	public AuthController(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	@PostMapping("/login")
	public ResponseEntity<?> iniciarSesion(@RequestBody Map<String, String> datos) {
		String correo = datos.get("correo");
		String contrasena = datos.get("contrasena");

		if (correo == null || correo.trim().isEmpty() || contrasena == null || contrasena.isEmpty()) {
			return ResponseEntity.badRequest().body("El correo y la contraseña son obligatorios");
		}

		Optional<UsuarioEntity> usuarioOpt = usuarioRepository.findByCorreo(correo.trim().toLowerCase());
		if (usuarioOpt.isEmpty() || !usuarioOpt.get().getContrasena().equals(Encriptador.sha256(contrasena))) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Correo o contraseña incorrectos");
		}

		UsuarioEntity usuario = usuarioOpt.get();
		Map<String, Object> respuesta = new HashMap<>();
		respuesta.put("id", usuario.getId());
		respuesta.put("nombre", usuario.getNombre());
		respuesta.put("correo", usuario.getCorreo());
		respuesta.put("codigoEvaluadora", usuario.getCodigoEvaluadora());
		return ResponseEntity.ok(respuesta);
	}
}
