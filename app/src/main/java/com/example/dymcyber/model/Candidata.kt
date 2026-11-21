package com.example.dymcyber.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "candidatas")
data class Candidata(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val aniosExperiencia: Int,
    val perfilUrl: String,
    val estado: String = "Pendiente",
    val fechaRegistro: Long = System.currentTimeMillis(),
    val cvNombre: String? = null,
    val cvUri: String? = null
)
