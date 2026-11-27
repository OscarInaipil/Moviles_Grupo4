package com.example.dymcyber.repository

import com.example.dymcyber.model.Candidata
import com.example.dymcyber.model.Evaluacion

class CandidataRepository(
    private val apiService: WimApiService = RetrofitClient.wimApiService
) {

    suspend fun obtenerTodas(): List<Candidata> {
        val respuesta = apiService.obtenerCandidatas()
        if (respuesta.isSuccessful) {
            return respuesta.body().orEmpty()
        }
        throw IllegalStateException("Error al obtener candidatas: ${respuesta.code()}")
    }

    suspend fun insertar(candidata: Candidata): Candidata? {
        val respuesta = apiService.crearCandidata(candidata)
        return if (respuesta.isSuccessful) {
            respuesta.body()
        } else {
            null
        }
    }

    suspend fun obtenerPorId(id: Long): Candidata? {
        val respuesta = apiService.obtenerCandidataPorId(id)
        return if (respuesta.isSuccessful) {
            respuesta.body()
        } else {
            null
        }
    }

    suspend fun actualizar(candidata: Candidata): Candidata? {
        val respuesta = apiService.actualizarCandidata(candidata.id, candidata)
        return if (respuesta.isSuccessful) {
            respuesta.body()
        } else {
            null
        }
    }

    suspend fun eliminar(id: Long): Boolean {
        val respuesta = apiService.eliminarCandidata(id)
        return respuesta.isSuccessful
    }

    suspend fun obtenerEvaluacion(candidataId: Long): Evaluacion? {
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
        listaPrueba.forEach { candidata ->
            try {
                apiService.crearCandidata(candidata)
            } catch (e: Exception) {
            }
        }
    }
}
