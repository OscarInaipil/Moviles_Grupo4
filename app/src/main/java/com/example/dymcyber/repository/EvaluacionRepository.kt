package com.example.dymcyber.repository

import com.example.dymcyber.model.Candidata
import com.example.dymcyber.model.Evaluacion
import com.example.dymcyber.model.ResultadoEvaluacion
import kotlinx.coroutines.flow.Flow

class EvaluacionRepository(
    private val evaluacionDao: EvaluacionDao,
    private val candidataDao: CandidataDao
) {

    val todasLasEvaluaciones: Flow<List<Evaluacion>> = evaluacionDao.obtenerTodas()

    val resultadosConsolidados: Flow<List<ResultadoEvaluacion>> = evaluacionDao.obtenerResultadosConsolidados()

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
        return try {
            if (evaluacionDao.existeEvaluacionParaCandidata(candidata.id)) {
                return false
            }
            evaluacionDao.insertarEvaluacion(evaluacion)
            candidataDao.actualizarCandidata(candidata.copy(estado = "Evaluada"))
            true
        } catch (e: Exception) {
            false
        }
    }
}
