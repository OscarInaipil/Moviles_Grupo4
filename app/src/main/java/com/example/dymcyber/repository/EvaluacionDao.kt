package com.example.dymcyber.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dymcyber.model.Evaluacion
import com.example.dymcyber.model.ResultadoEvaluacion
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

    @Query(
        """
        SELECT c.id AS candidataId, c.nombre AS nombre, e.evaluadoraId AS evaluadoraId,
            e.notaLiderazgo AS notaLiderazgo, e.notaTrayectoria AS notaTrayectoria,
            e.notaImpacto AS notaImpacto, e.notaInnovacion AS notaInnovacion,
            e.puntajePromedio AS puntajePromedio, e.observaciones AS observaciones,
            e.fechaEvaluacion AS fechaEvaluacion
        FROM candidatas c
        INNER JOIN evaluaciones e ON e.candidataId = c.id
        WHERE c.estado = 'Evaluada'
        ORDER BY e.puntajePromedio DESC, e.fechaEvaluacion DESC
        """
    )
    fun obtenerResultadosConsolidados(): Flow<List<ResultadoEvaluacion>>
}
