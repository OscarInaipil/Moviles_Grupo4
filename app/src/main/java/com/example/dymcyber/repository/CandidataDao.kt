package com.example.dymcyber.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.dymcyber.model.Candidata
import kotlinx.coroutines.flow.Flow

@Dao
interface CandidataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCandidata(candidata: Candidata): Long

    @Update
    suspend fun actualizarCandidata(candidata: Candidata): Int

    @Delete
    suspend fun eliminarCandidata(candidata: Candidata): Int

    @Query("SELECT * FROM candidatas ORDER BY id DESC")
    fun obtenerTodas(): Flow<List<Candidata>>

    @Query("SELECT * FROM candidatas WHERE id = :id")
    suspend fun obtenerPorId(id: Long): Candidata?

    @Query("SELECT COUNT(*) FROM candidatas")
    suspend fun contarCandidatas(): Int
}
