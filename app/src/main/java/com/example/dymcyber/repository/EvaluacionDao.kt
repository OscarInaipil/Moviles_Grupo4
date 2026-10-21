package com.example.dymcyber.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dymcyber.model.Evaluacion
import kotlinx.coroutines.flow.Flow

@Dao
interface EvaluacionDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertarEvaluacion(evaluacion: Evaluacion): Long

    @Query("SELECT * FROM evaluaciones WHERE candidataId = :candidataId LIMIT 1")
    suspend fun obtenerPorCandidataId(candidataId: Long): Evaluacion?

    @Query("SELECT * FROM evaluaciones WHERE candidataId = :candidataId LIMIT 1")
    fun observarPorCandidataId(candidataId: Long): Flow<Evaluacion?>

    @Query("SELECT * FROM evaluaciones ORDER BY fechaEvaluacion DESC")
    fun obtenerTodas(): Flow<List<Evaluacion>>

    @Query("SELECT EXISTS(SELECT 1 FROM evaluaciones WHERE candidataId = :candidataId)")
    suspend fun existeEvaluacionParaCandidata(candidataId: Long): Boolean
}
