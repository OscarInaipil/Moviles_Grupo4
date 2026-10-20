package com.example.dymcyber.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.dymcyber.model.Candidata
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para operaciones de Room sobre la tabla candidatas.
 */
@Dao
interface CandidataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCandidata(candidata: Candidata): Long

    @Update
    suspend fun actualizarCandidata(candidata: Candidata): Int

    @Query("SELECT * FROM candidatas ORDER BY id DESC")
    fun obtenerTodas(): Flow<List<Candidata>>

    @Query("SELECT * FROM candidatas WHERE id = :id")
    suspend fun obtenerPorId(id: Long): Candidata?

    @Query("SELECT COUNT(*) FROM candidatas")
    suspend fun contarCandidatas(): Int
}
