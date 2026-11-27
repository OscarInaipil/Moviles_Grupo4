package com.example.dymcyber.model

data class Candidata(
    val id: Long = 0,
    val nombre: String,
    val aniosExperiencia: Int,
    val perfilUrl: String,
    val estado: String = "Pendiente",
    val fechaRegistro: Long = System.currentTimeMillis(),
    val cvNombre: String? = null,
    val cvUri: String? = null
)
