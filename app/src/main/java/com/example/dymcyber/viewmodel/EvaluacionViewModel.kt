package com.example.dymcyber.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dymcyber.model.Candidata
import com.example.dymcyber.model.Evaluacion
import com.example.dymcyber.repository.AppDatabase
import com.example.dymcyber.repository.CandidataRepository
import com.example.dymcyber.repository.EvaluacionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MatrizEvaluacionState(
    val candidata: Candidata? = null,
    val notaLiderazgo: Int = 3,
    val notaTrayectoria: Int = 3,
    val notaImpacto: Int = 3,
    val notaInnovacion: Int = 3,
    val puntajePromedio: Double = 3.0,
    val evaluadoraId: String = "",
    val evaluadoraIdError: String? = null,
    val observaciones: String = "",
    val observacionesError: String? = null,
    val yaEvaluada: Boolean = false,
    val evaluacionExistente: Evaluacion? = null,
    val guardadoExitoso: Boolean = false,
    val guardando: Boolean = false,
    val cargando: Boolean = true
)

class EvaluacionViewModel(application: Application) : AndroidViewModel(application) {

    private val evaluacionRepository: EvaluacionRepository
    private val candidataRepository: CandidataRepository

    init {
        val db = AppDatabase.getDatabase(application)
        evaluacionRepository = EvaluacionRepository(db.evaluacionDao(), db.candidataDao())
        candidataRepository = CandidataRepository(db.candidataDao())
    }

    private val _estado = MutableStateFlow(MatrizEvaluacionState())
    val estado: StateFlow<MatrizEvaluacionState> = _estado.asStateFlow()

    fun cargarCandidata(candidataId: Long) {
        viewModelScope.launch {
            _estado.value = _estado.value.copy(cargando = true)
            val candidata = candidataRepository.obtenerPorId(candidataId)
            val evaluacionPrevia = evaluacionRepository.obtenerPorCandidataId(candidataId)

            if (evaluacionPrevia != null) {
                _estado.value = MatrizEvaluacionState(
                    candidata = candidata,
                    notaLiderazgo = evaluacionPrevia.notaLiderazgo,
                    notaTrayectoria = evaluacionPrevia.notaTrayectoria,
                    notaImpacto = evaluacionPrevia.notaImpacto,
                    notaInnovacion = evaluacionPrevia.notaInnovacion,
                    puntajePromedio = evaluacionPrevia.puntajePromedio,
                    evaluadoraId = evaluacionPrevia.evaluadoraId,
                    observaciones = evaluacionPrevia.observaciones,
                    yaEvaluada = true,
                    evaluacionExistente = evaluacionPrevia,
                    cargando = false
                )
            } else {
                _estado.value = MatrizEvaluacionState(
                    candidata = candidata,
                    cargando = false
                )
            }
        }
    }

    fun setNotaLiderazgo(nota: Int) {
        val nuevoEstado = _estado.value.copy(notaLiderazgo = nota)
        _estado.value = nuevoEstado.copy(puntajePromedio = calcularPromedio(nuevoEstado))
    }

    fun setNotaTrayectoria(nota: Int) {
        val nuevoEstado = _estado.value.copy(notaTrayectoria = nota)
        _estado.value = nuevoEstado.copy(puntajePromedio = calcularPromedio(nuevoEstado))
    }

    fun setNotaImpacto(nota: Int) {
        val nuevoEstado = _estado.value.copy(notaImpacto = nota)
        _estado.value = nuevoEstado.copy(puntajePromedio = calcularPromedio(nuevoEstado))
    }

    fun setNotaInnovacion(nota: Int) {
        val nuevoEstado = _estado.value.copy(notaInnovacion = nota)
        _estado.value = nuevoEstado.copy(puntajePromedio = calcularPromedio(nuevoEstado))
    }

    fun onEvaluadoraIdChange(id: String) {
        _estado.value = _estado.value.copy(
            evaluadoraId = id,
            evaluadoraIdError = null
        )
    }

    fun onObservacionesChange(texto: String) {
        _estado.value = _estado.value.copy(
            observaciones = texto,
            observacionesError = null
        )
    }

    private fun calcularPromedio(estado: MatrizEvaluacionState): Double {
        val suma = estado.notaLiderazgo + estado.notaTrayectoria + estado.notaImpacto + estado.notaInnovacion
        val promedio = suma / 4.0
        return Math.round(promedio * 10.0) / 10.0
    }

    fun validarYGuardarEvaluacion(): Boolean {
        val actual = _estado.value
        val candidata = actual.candidata ?: return false

        if (actual.guardando || actual.yaEvaluada) {
            return false
        }

        var errorEvaluadora: String? = null
        var errorObs: String? = null

        if (actual.evaluadoraId.trim().isEmpty()) {
            errorEvaluadora = "El identificador de la evaluadora es obligatorio"
        } else if (actual.evaluadoraId.trim().length < 3) {
            errorEvaluadora = "Debe tener al menos 3 caracteres (ej: EVA-01)"
        }

        if (actual.observaciones.trim().isEmpty()) {
            errorObs = "La justificación de la evaluación es obligatoria"
        } else if (actual.observaciones.trim().length < 10) {
            errorObs = "La justificación debe tener al menos 10 caracteres"
        }

        val esValido = errorEvaluadora == null && errorObs == null

        if (!esValido) {
            _estado.value = actual.copy(
                evaluadoraIdError = errorEvaluadora,
                observacionesError = errorObs,
                guardadoExitoso = false
            )
            return false
        }

        _estado.value = actual.copy(guardando = true)

        viewModelScope.launch {
            try {
                val evaluacion = Evaluacion(
                    candidataId = candidata.id,
                    evaluadoraId = actual.evaluadoraId.trim(),
                    notaLiderazgo = actual.notaLiderazgo,
                    notaTrayectoria = actual.notaTrayectoria,
                    notaImpacto = actual.notaImpacto,
                    notaInnovacion = actual.notaInnovacion,
                    puntajePromedio = actual.puntajePromedio,
                    observaciones = actual.observaciones.trim()
                )

                val guardado = evaluacionRepository.guardarEvaluacion(evaluacion, candidata)
                if (guardado) {
                    _estado.value = _estado.value.copy(
                        guardadoExitoso = true,
                        yaEvaluada = true,
                        evaluacionExistente = evaluacion,
                        guardando = false
                    )
                } else {
                    _estado.value = _estado.value.copy(guardando = false)
                }
            } catch (e: Exception) {
                _estado.value = _estado.value.copy(guardando = false)
            }
        }

        return true
    }

    fun reiniciarEstadoGuardado() {
        _estado.value = _estado.value.copy(guardadoExitoso = false)
    }
}
