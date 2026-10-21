package com.example.dymcyber.repository

import com.example.dymcyber.model.Candidata
import com.example.dymcyber.model.Evaluacion
import kotlinx.coroutines.flow.Flow

class EvaluacionRepository(
    private val evaluacionDao: EvaluacionDao,
    private val candidataDao: CandidataDao
) {

    val todasLasEvaluaciones: Flow<List<Evaluacion>> = evaluacionDao.obtenerTodas()

    suspend fun obtenerPorCandidataId(candidataId: Long): Evaluacion? {
        return evaluacionDao.obtenerPorCandidataId(candidataId)
    }

    fun observarPorCandidataId(candidataId: Long): Flow<Evaluacion?> {
        return evaluacionDao.observarPorCandidataId(candidataId)
    }

    suspend fun yaEstaEvaluada(candidataId: Long): Boolean {
        return evaluacionDao.existeEvaluacionParaCandidata(candidataId)
    }

    suspend fun guardarEvaluacion(evaluacion: Evaluacion, candidata: Candidata): Boolean {
        if (evaluacionDao.existeEvaluacionParaCandidata(candidata.id)) {
            return false
        }
        evaluacionDao.insertarEvaluacion(evaluacion)
        candidataDao.actualizarCandidata(candidata.copy(estado = "Evaluada"))
        return true
    }
}
