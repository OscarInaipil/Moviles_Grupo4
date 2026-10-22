package com.example.dymcyber.model

data class ResultadoEvaluacion(
    val candidataId: Long,
    val nombre: String,
    val evaluadoraId: String,
    val notaLiderazgo: Int,
    val notaTrayectoria: Int,
    val notaImpacto: Int,
    val notaInnovacion: Int,
    val puntajePromedio: Double,
    val observaciones: String,
    val fechaEvaluacion: Long
)
