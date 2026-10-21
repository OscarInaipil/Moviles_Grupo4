package com.example.dymcyber.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "evaluaciones",
    foreignKeys = [
        ForeignKey(
            entity = Candidata::class,
            parentColumns = ["id"],
            childColumns = ["candidataId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["candidataId"], unique = true)
    ]
)
data class Evaluacion(
    @PrimaryKey(autoGenerate = true)
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
