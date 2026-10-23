package com.example.dymcyber.repository

import com.example.dymcyber.model.Candidata
import com.example.dymcyber.model.Evaluacion
import kotlinx.coroutines.flow.Flow

class CandidataRepository(
    private val candidataDao: CandidataDao,
    private val evaluacionDao: EvaluacionDao? = null
) {

    val todasLasCandidatas: Flow<List<Candidata>> = candidataDao.obtenerTodas()

    suspend fun insertar(candidata: Candidata): Long {
        return candidataDao.insertarCandidata(candidata)
    }

    suspend fun obtenerPorId(id: Long): Candidata? {
        return candidataDao.obtenerPorId(id)
    }

    suspend fun actualizar(candidata: Candidata): Int {
        return candidataDao.actualizarCandidata(candidata)
    }

    suspend fun obtenerEvaluacion(candidataId: Long): Evaluacion? {
        return evaluacionDao?.obtenerPorCandidataId(candidataId)
    }

    suspend fun precargarCandidatasPrueba() {
        val listaPrueba = listOf(
            Candidata(
                nombre = "Elena Torres Muñoz",
                aniosExperiencia = 8,
                perfilUrl = "https://linkedin.com/in/elena-torres-wim",
                estado = "Pendiente"
            ),
            Candidata(
                nombre = "Valentina Vega Rojas",
                aniosExperiencia = 5,
                perfilUrl = "https://linkedin.com/in/valentina-vega-wim",
                estado = "Pendiente"
            ),
            Candidata(
                nombre = "Patricia Morales Silva",
                aniosExperiencia = 12,
                perfilUrl = "https://linkedin.com/in/patricia-morales-wim",
                estado = "Pendiente"
            )
        )
        listaPrueba.forEach { candidataDao.insertarCandidata(it) }
    }
}
