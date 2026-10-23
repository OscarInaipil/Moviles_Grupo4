package com.example.dymcyber

import org.junit.Assert.assertEquals
import org.junit.Test

class CalculoEvaluacionTest {

    private fun calcularPromedioMatriz(liderazgo: Int, trayectoria: Int, impacto: Int, innovacion: Int): Double {
        val suma = liderazgo + trayectoria + impacto + innovacion
        val promedio = suma / 4.0
        return Math.round(promedio * 10.0) / 10.0
    }

    private fun calcularPromedioGeneral(lista: List<Double>): Double {
        if (lista.isEmpty()) {
            return 0.0
        }
        val promedio = lista.sum() / lista.size
        return Math.round(promedio * 10.0) / 10.0
    }

    @Test
    fun promedioNotasHomogeneasRetornaPuntajeExacto() {
        val resultado = calcularPromedioMatriz(4, 4, 4, 4)
        assertEquals(4.0, resultado, 0.001)
    }

    @Test
    fun promedioConDecimalesRedondeaAUnDecimal() {
        val resultado = calcularPromedioMatriz(3, 3, 4, 3)
        assertEquals(3.3, resultado, 0.001)
    }

    @Test
    fun promedioMinimoPermitidoEsUno() {
        val resultado = calcularPromedioMatriz(1, 1, 1, 1)
        assertEquals(1.0, resultado, 0.001)
    }

    @Test
    fun promedioMaximoPermitidoEsCinco() {
        val resultado = calcularPromedioMatriz(5, 5, 5, 5)
        assertEquals(5.0, resultado, 0.001)
    }

    @Test
    fun promedioGeneralListaVaciaRetornaCero() {
        val resultado = calcularPromedioGeneral(emptyList())
        assertEquals(0.0, resultado, 0.001)
    }

    @Test
    fun promedioGeneralCalculaCorrectamenteVariasCandidatas() {
        val puntajes = listOf(4.5, 3.8, 4.2)
        val resultado = calcularPromedioGeneral(puntajes)
        assertEquals(4.2, resultado, 0.001)
    }
}
