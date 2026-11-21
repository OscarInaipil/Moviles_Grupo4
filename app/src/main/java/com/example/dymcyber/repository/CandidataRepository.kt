package com.example.dymcyber.repository

import com.example.dymcyber.model.Candidata
import com.example.dymcyber.model.Evaluacion
import kotlinx.coroutines.flow.Flow

class CandidataRepository(
    private val candidataDao: CandidataDao,
    private val evaluacionDao: EvaluacionDao? = null,
    private val apiService: WimApiService = RetrofitClient.wimApiService
) {

    val todasLasCandidatas: Flow<List<Candidata>> = candidataDao.obtenerTodas()

    suspend fun sincronizarConBackend() {
        try {
            val respuesta = apiService.obtenerCandidatas()
            if (respuesta.isSuccessful) {
                val remotas = respuesta.body().orEmpty()
                for (candidata in remotas) {
                    val local = candidataDao.obtenerPorId(candidata.id)
                    if (local == null) {
                        candidataDao.insertarCandidata(candidata)
                    } else {
                        candidataDao.actualizarCandidata(candidata)
                    }
                }
            }
        } catch (e: Exception) {
        }
    }

    suspend fun insertar(candidata: Candidata): Long {
        val idLocal = candidataDao.insertarCandidata(candidata)
        try {
            apiService.crearCandidata(candidata.copy(id = idLocal))
        } catch (e: Exception) {
        }
        return idLocal
    }

    suspend fun obtenerPorId(id: Long): Candidata? {
        try {
            val respuesta = apiService.obtenerCandidataPorId(id)
            if (respuesta.isSuccessful && respuesta.body() != null) {
                val remota = respuesta.body()!!
                candidataDao.actualizarCandidata(remota)
                return remota
            }
        } catch (e: Exception) {
        }
        return candidataDao.obtenerPorId(id)
    }

    suspend fun actualizar(candidata: Candidata): Int {
        val resultadoLocal = candidataDao.actualizarCandidata(candidata)
        try {
            apiService.actualizarCandidata(candidata.id, candidata)
        } catch (e: Exception) {
        }
        return resultadoLocal
    }

    suspend fun eliminar(candidata: Candidata): Int {
        val resultado = candidataDao.eliminarCandidata(candidata)
        try {
            apiService.eliminarCandidata(candidata.id)
        } catch (e: Exception) {
        }
        return resultado
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
        listaPrueba.forEach { candidata ->
            val id = candidataDao.insertarCandidata(candidata)
            try {
                apiService.crearCandidata(candidata.copy(id = id))
            } catch (e: Exception) {
            }
        }
    }
}
