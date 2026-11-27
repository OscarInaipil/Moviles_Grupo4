package com.example.dymcyber.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dymcyber.model.Candidata
import com.example.dymcyber.model.Evaluacion
import com.example.dymcyber.repository.CandidataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FormularioRegistroState(
    val nombre: String = "",
    val nombreError: String? = null,
    val aniosExperiencia: String = "",
    val aniosExperienciaError: String? = null,
    val perfilUrl: String = "",
    val perfilUrlError: String? = null,
    val errorServidor: String? = null,
    val guardadoExitoso: Boolean = false,
    val guardando: Boolean = false
)

class CandidataViewModel(
    private val repository: CandidataRepository = CandidataRepository()
) : ViewModel() {

    private val _candidatas = MutableStateFlow<List<Candidata>?>(null)
    val candidatas: StateFlow<List<Candidata>?> = _candidatas.asStateFlow()

    private val _errorLista = MutableStateFlow<String?>(null)
    val errorLista: StateFlow<String?> = _errorLista.asStateFlow()

    private val _candidataSeleccionada = MutableStateFlow<Candidata?>(null)
    val candidataSeleccionada: StateFlow<Candidata?> = _candidataSeleccionada.asStateFlow()

    private val _evaluacionCandidata = MutableStateFlow<Evaluacion?>(null)
    val evaluacionCandidata: StateFlow<Evaluacion?> = _evaluacionCandidata.asStateFlow()

    private val _errorDetalle = MutableStateFlow<String?>(null)
    val errorDetalle: StateFlow<String?> = _errorDetalle.asStateFlow()

    private val _formulario = MutableStateFlow(FormularioRegistroState())
    val formulario: StateFlow<FormularioRegistroState> = _formulario.asStateFlow()

    fun cargarCandidatas() {
        viewModelScope.launch {
            _errorLista.value = null
            try {
                _candidatas.value = repository.obtenerTodas()
            } catch (e: Exception) {
                _errorLista.value = "No se pudo conectar con el servidor"
                if (_candidatas.value == null) {
                    _candidatas.value = emptyList()
                }
            }
        }
    }

    fun cargarCandidata(id: Long) {
        viewModelScope.launch {
            _candidataSeleccionada.value = null
            _evaluacionCandidata.value = null
            _errorDetalle.value = null
            try {
                val encontrada = repository.obtenerPorId(id)
                if (encontrada != null) {
                    _candidataSeleccionada.value = encontrada
                    _evaluacionCandidata.value = repository.obtenerEvaluacion(id)
                } else {
                    _errorDetalle.value = "No se encontró la candidata en el servidor"
                }
            } catch (e: Exception) {
                _errorDetalle.value = "No se pudo conectar con el servidor"
            }
        }
    }

    fun adjuntarCv(nombreArchivo: String, uriArchivo: String) {
        val candidataActual = _candidataSeleccionada.value ?: return
        val candidataConCv = candidataActual.copy(
            cvNombre = nombreArchivo,
            cvUri = uriArchivo
        )
        viewModelScope.launch {
            try {
                val actualizada = repository.actualizar(candidataConCv)
                if (actualizada != null) {
                    _candidataSeleccionada.value = actualizada
                    cargarCandidatas()
                }
            } catch (e: Exception) {
                _errorDetalle.value = "No se pudo actualizar el CV en el servidor"
            }
        }
    }

    fun eliminarCandidata(alTerminar: () -> Unit) {
        val candidataActual = _candidataSeleccionada.value ?: return
        viewModelScope.launch {
            try {
                val eliminada = repository.eliminar(candidataActual.id)
                if (eliminada) {
                    _candidataSeleccionada.value = null
                    _evaluacionCandidata.value = null
                    cargarCandidatas()
                    alTerminar()
                } else {
                    _errorDetalle.value = "No se pudo eliminar la candidata"
                }
            } catch (e: Exception) {
                _errorDetalle.value = "No se pudo conectar con el servidor"
            }
        }
    }

    fun onNombreChange(nuevoNombre: String) {
        _formulario.value = _formulario.value.copy(
            nombre = nuevoNombre,
            nombreError = null,
            errorServidor = null
        )
    }

    fun onAniosExperienciaChange(nuevosAnios: String) {
        _formulario.value = _formulario.value.copy(
            aniosExperiencia = nuevosAnios,
            aniosExperienciaError = null,
            errorServidor = null
        )
    }

    fun onPerfilUrlChange(nuevaUrl: String) {
        _formulario.value = _formulario.value.copy(
            perfilUrl = nuevaUrl,
            perfilUrlError = null,
            errorServidor = null
        )
    }

    fun validarYRegistrar(): Boolean {
        val estadoActual = _formulario.value

        if (estadoActual.guardando) {
            return false
        }

        var errorNombre: String? = null
        var errorExperiencia: String? = null
        var errorUrl: String? = null

        if (estadoActual.nombre.trim().isEmpty()) {
            errorNombre = "El nombre de la postulante es obligatorio"
        } else if (estadoActual.nombre.trim().length < 3) {
            errorNombre = "El nombre debe tener al menos 3 caracteres"
        }

        val expInt = estadoActual.aniosExperiencia.trim().toIntOrNull()
        if (estadoActual.aniosExperiencia.trim().isEmpty()) {
            errorExperiencia = "Los años de experiencia son obligatorios"
        } else if (expInt == null || expInt < 0) {
            errorExperiencia = "Ingrese un número válido mayor o igual a 0"
        }

        if (estadoActual.perfilUrl.trim().isEmpty()) {
            errorUrl = "El enlace a perfil profesional es obligatorio"
        } else if (!estadoActual.perfilUrl.startsWith("http://") && !estadoActual.perfilUrl.startsWith("https://")) {
            errorUrl = "La URL debe iniciar con http:// o https://"
        }

        val esValido = errorNombre == null && errorExperiencia == null && errorUrl == null

        if (!esValido) {
            _formulario.value = estadoActual.copy(
                nombreError = errorNombre,
                aniosExperienciaError = errorExperiencia,
                perfilUrlError = errorUrl,
                guardadoExitoso = false
            )
            return false
        }

        _formulario.value = estadoActual.copy(guardando = true, errorServidor = null)

        viewModelScope.launch {
            try {
                val nuevaCandidata = Candidata(
                    nombre = estadoActual.nombre.trim(),
                    aniosExperiencia = expInt ?: 0,
                    perfilUrl = estadoActual.perfilUrl.trim(),
                    estado = "Pendiente"
                )
                val creada = repository.insertar(nuevaCandidata)
                if (creada != null) {
                    _formulario.value = FormularioRegistroState(guardadoExitoso = true, guardando = false)
                    cargarCandidatas()
                } else {
                    _formulario.value = _formulario.value.copy(
                        guardando = false,
                        errorServidor = "El servidor rechazó el registro"
                    )
                }
            } catch (e: Exception) {
                _formulario.value = _formulario.value.copy(
                    guardando = false,
                    errorServidor = "No se pudo conectar con el servidor"
                )
            }
        }

        return true
    }

    fun reiniciarEstadoGuardado() {
        _formulario.value = _formulario.value.copy(guardadoExitoso = false)
    }

    fun cargarCandidatasPrueba() {
        viewModelScope.launch {
            repository.precargarCandidatasPrueba()
            cargarCandidatas()
        }
    }

    fun limpiarEstado() {
        _candidatas.value = null
        _errorLista.value = null
        _candidataSeleccionada.value = null
        _evaluacionCandidata.value = null
        _errorDetalle.value = null
        _formulario.value = FormularioRegistroState()
    }
}
