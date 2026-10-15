package com.example.dymcyber.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room que representa a una postulante del reconocimiento WIM 100.
 * Utiliza exclusivamente datos sintéticos, ficticios o anonimizados.
 */
@Entity(tableName = "candidatas")
data class Candidata(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val aniosExperiencia: Int,
    val perfilUrl: String,
    val estado: String = "Pendiente", // "Pendiente" o "Evaluada"
    val fechaRegistro: Long = System.currentTimeMillis()
)
