package com.example.dymcyber.repository

import com.example.dymcyber.model.Candidata
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio simple para intermediar entre el DAO y el ViewModel.
 */
class CandidataRepository(private val candidataDao: CandidataDao) {

    val todasLasCandidatas: Flow<List<Candidata>> = candidataDao.obtenerTodas()

    suspend fun insertar(candidata: Candidata): Long {
        return candidataDao.insertarCandidata(candidata)
    }

    suspend fun obtenerPorId(id: Long): Candidata? {
        return candidataDao.obtenerPorId(id)
    }
}
