package com.example.dymcyber

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidacionFormulariosTest {

    private fun validarCandidata(nombre: String, experiencia: String, perfilUrl: String): Boolean {
        if (nombre.trim().length < 3) return false
        val expInt = experiencia.trim().toIntOrNull() ?: return false
        if (expInt < 0) return false
        val url = perfilUrl.trim()
        if (!url.startsWith("http://") && !url.startsWith("https://")) return false
        return true
    }

    private fun validarEvaluacion(evaluadoraId: String, observaciones: String): Boolean {
        if (evaluadoraId.trim().length < 3) return false
        if (observaciones.trim().length < 10) return false
        return true
    }

    @Test
    fun nombreVacioOInferiorATresCaracteresFalla() {
        assertFalse(validarCandidata("", "5", "https://linkedin.com/in/test"))
        assertFalse(validarCandidata("Ab", "5", "https://linkedin.com/in/test"))
    }

    @Test
    fun experienciaInvalidaFalla() {
        assertFalse(validarCandidata("Elena Torres", "", "https://linkedin.com/in/test"))
        assertFalse(validarCandidata("Elena Torres", "-1", "https://linkedin.com/in/test"))
        assertFalse(validarCandidata("Elena Torres", "cinco", "https://linkedin.com/in/test"))
    }

    @Test
    fun urlSinProtocoloFalla() {
        assertFalse(validarCandidata("Elena Torres", "5", "linkedin.com/in/test"))
        assertFalse(validarCandidata("Elena Torres", "5", "www.linkedin.com/in/test"))
    }

    @Test
    fun candidataConDatosValidosPasaExitosamente() {
        assertTrue(validarCandidata("Elena Torres", "8", "https://linkedin.com/in/elena-torres"))
    }

    @Test
    fun evaluadoraIncompletaFalla() {
        assertFalse(validarEvaluacion("", "Justificación detallada de la evaluación"))
        assertFalse(validarEvaluacion("EV", "Justificación detallada de la evaluación"))
    }

    @Test
    fun observacionesMenorADiezCaracteresFalla() {
        assertFalse(validarEvaluacion("EVA-01", "Muy buena"))
    }

    @Test
    fun evaluacionConDatosValidosPasaExitosamente() {
        assertTrue(validarEvaluacion("EVA-01", "La candidata demuestra gran liderazgo e impacto en su equipo"))
    }
}
