package com.example.dymcyber.repository

import com.example.dymcyber.model.Evaluacion
import com.example.dymcyber.model.ResultadoEvaluacion

class EvaluacionRepository(
    private val apiService: WimApiService = RetrofitClient.wimApiService
) {

    suspend fun obtenerPorCandidataId(candidataId: Long): Evaluacion? {
        return try {
            val respuesta = apiService.obtenerEvaluacionPorCandidata(candidataId)
            if (respuesta.isSuccessful) {
                respuesta.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun guardarEvaluacion(evaluacion: Evaluacion): Evaluacion? {
        return try {
            val respuesta = apiService.crearEvaluacion(evaluacion)
            if (respuesta.isSuccessful) {
                respuesta.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun obtenerResultadosConsolidados(): List<ResultadoEvaluacion> {
        val respEvaluaciones = apiService.obtenerEvaluaciones()
        val respCandidatas = apiService.obtenerCandidatas()
        if (!respEvaluaciones.isSuccessful || !respCandidatas.isSuccessful) {
            throw IllegalStateException("Error al obtener resultados consolidados")
        }
        val candidatasPorId = respCandidatas.body().orEmpty().associateBy { it.id }
        return respEvaluaciones.body().orEmpty().mapNotNull { evaluacion ->
            val candidata = candidatasPorId[evaluacion.candidataId] ?: return@mapNotNull null
            ResultadoEvaluacion(
                candidataId = evaluacion.candidataId,
                nombre = candidata.nombre,
                evaluadoraId = evaluacion.evaluadoraId,
                notaLiderazgo = evaluacion.notaLiderazgo,
                notaTrayectoria = evaluacion.notaTrayectoria,
                notaImpacto = evaluacion.notaImpacto,
                notaInnovacion = evaluacion.notaInnovacion,
                puntajePromedio = evaluacion.puntajePromedio,
                observaciones = evaluacion.observaciones,
                fechaEvaluacion = evaluacion.fechaEvaluacion
            )
        }
    }
}
