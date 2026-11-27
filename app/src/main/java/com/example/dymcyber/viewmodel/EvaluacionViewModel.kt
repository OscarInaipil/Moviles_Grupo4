package com.example.dymcyber.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dymcyber.model.Candidata
import com.example.dymcyber.model.Evaluacion
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
    val errorServidor: String? = null,
    val guardadoExitoso: Boolean = false,
    val guardando: Boolean = false,
    val cargando: Boolean = true
)

class EvaluacionViewModel(
    private val evaluacionRepository: EvaluacionRepository = EvaluacionRepository(),
    private val candidataRepository: CandidataRepository = CandidataRepository()
) : ViewModel() {

    private val _estado = MutableStateFlow(MatrizEvaluacionState())
    val estado: StateFlow<MatrizEvaluacionState> = _estado.asStateFlow()

    fun cargarCandidata(candidataId: Long, codigoEvaluadoraDefecto: String = "") {
        viewModelScope.launch {
            _estado.value = _estado.value.copy(cargando = true, errorServidor = null)
            try {
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
                        evaluadoraId = codigoEvaluadoraDefecto,
                        cargando = false
                    )
                }
            } catch (e: Exception) {
                _estado.value = MatrizEvaluacionState(
                    cargando = false,
                    errorServidor = "No se pudo conectar con el servidor"
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
            evaluadoraIdError = null,
            errorServidor = null
        )
    }

    fun onObservacionesChange(texto: String) {
        _estado.value = _estado.value.copy(
            observaciones = texto,
            observacionesError = null,
            errorServidor = null
        )
    }

    private fun calcularPromedio(estado: MatrizEvaluacionState): Double {
        val suma = estado.notaLiderazgo + estado.notaTrayectoria + estado.notaImpacto + estado.notaInnovacion
        val promedio = suma / 4.0
        return Math.round(promedio * 10.0) / 10.0
    }

    fun validarYGuardarEvaluacion(alGuardar: () -> Unit = {}): Boolean {
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

        _estado.value = actual.copy(guardando = true, errorServidor = null)

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

                val guardada = evaluacionRepository.guardarEvaluacion(evaluacion)
                if (guardada != null) {
                    _estado.value = _estado.value.copy(
                        guardadoExitoso = true,
                        yaEvaluada = true,
                        evaluacionExistente = guardada,
                        guardando = false
                    )
                    alGuardar()
                } else {
                    _estado.value = _estado.value.copy(
                        guardando = false,
                        errorServidor = "No se pudo guardar la evaluación en el servidor"
                    )
                }
            } catch (e: Exception) {
                _estado.value = _estado.value.copy(
                    guardando = false,
                    errorServidor = "No se pudo conectar con el servidor"
                )
            }
        }

        return true
    }

    fun reiniciarEstadoGuardado() {
        _estado.value = _estado.value.copy(guardadoExitoso = false)
    }
}
