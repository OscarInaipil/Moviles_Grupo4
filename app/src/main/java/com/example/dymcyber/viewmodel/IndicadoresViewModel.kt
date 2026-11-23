package com.example.dymcyber.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dymcyber.model.MindicadorRespuesta
import com.example.dymcyber.repository.IndicadoresRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class IndicadoresViewModel(
    private val repository: IndicadoresRepository = IndicadoresRepository()
) : ViewModel() {

    private val _indicadores = MutableStateFlow<MindicadorRespuesta?>(null)
    val indicadores: StateFlow<MindicadorRespuesta?> = _indicadores.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        cargarIndicadores()
    }

    fun cargarIndicadores() {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            try {
                val resultado = repository.obtenerIndicadores()
                if (resultado != null) {
                    _indicadores.value = resultado
                } else {
                    _error.value = "No se pudieron cargar los indicadores economicos"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al conectar con el servicio de indicadores"
            } finally {
                _cargando.value = false
            }
        }
    }
}
