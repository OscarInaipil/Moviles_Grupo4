package com.example.dymcyber.model

data class Evaluacion(
    val id: Long = 0,
    val candidataId: Long,
    val evaluadoraId: String,
    val notaLiderazgo: Int,
    val notaTrayectoria: Int,
    val notaImpacto: Int,
    val notaInnovacion: Int,
    val puntajePromedio: Double,
    val observaciones: String,
    val fechaEvaluacion: Long = System.currentTimeMillis()
)
