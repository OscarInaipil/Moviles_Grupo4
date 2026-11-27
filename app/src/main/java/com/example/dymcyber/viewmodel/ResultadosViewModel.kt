package com.example.dymcyber.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dymcyber.model.ResultadoEvaluacion
import com.example.dymcyber.repository.EvaluacionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ResultadosViewModel(
    private val repository: EvaluacionRepository = EvaluacionRepository()
) : ViewModel() {

    private val _resultados = MutableStateFlow<List<ResultadoEvaluacion>?>(null)
    val resultados: StateFlow<List<ResultadoEvaluacion>?> = _resultados.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun cargarResultados() {
        viewModelScope.launch {
            _error.value = null
            try {
                _resultados.value = repository.obtenerResultadosConsolidados()
            } catch (e: Exception) {
                _error.value = "No se pudieron cargar los resultados desde el servidor"
                if (_resultados.value == null) {
                    _resultados.value = emptyList()
                }
            }
        }
    }

    fun calcularPromedioGeneral(lista: List<ResultadoEvaluacion>): Double {
        if (lista.isEmpty()) {
            return 0.0
        }
        val promedio = lista.sumOf { it.puntajePromedio } / lista.size
        return Math.round(promedio * 10.0) / 10.0
    }
}
