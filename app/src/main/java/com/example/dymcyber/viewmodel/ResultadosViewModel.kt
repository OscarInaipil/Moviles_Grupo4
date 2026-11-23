package com.example.dymcyber.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dymcyber.model.ResultadoEvaluacion
import com.example.dymcyber.repository.AppDatabase
import com.example.dymcyber.repository.EvaluacionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ResultadosViewModel(
    application: Application,
    private val repository: EvaluacionRepository
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application,
        EvaluacionRepository(
            AppDatabase.getDatabase(application).evaluacionDao(),
            AppDatabase.getDatabase(application).candidataDao()
        )
    )

    val resultados: StateFlow<List<ResultadoEvaluacion>?> = repository.resultadosConsolidados
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun calcularPromedioGeneral(lista: List<ResultadoEvaluacion>): Double {
        if (lista.isEmpty()) {
            return 0.0
        }
        val promedio = lista.sumOf { it.puntajePromedio } / lista.size
        return Math.round(promedio * 10.0) / 10.0
    }
}
