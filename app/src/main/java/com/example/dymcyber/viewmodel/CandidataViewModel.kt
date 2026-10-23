package com.example.dymcyber.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dymcyber.model.Candidata
import com.example.dymcyber.model.Evaluacion
import com.example.dymcyber.repository.AppDatabase
import com.example.dymcyber.repository.CandidataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FormularioRegistroState(
    val nombre: String = "",
    val nombreError: String? = null,
    val aniosExperiencia: String = "",
    val aniosExperienciaError: String? = null,
    val perfilUrl: String = "",
    val perfilUrlError: String? = null,
    val guardadoExitoso: Boolean = false,
    val guardando: Boolean = false
)

class CandidataViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CandidataRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = CandidataRepository(db.candidataDao(), db.evaluacionDao())
    }

    val candidatas: StateFlow<List<Candidata>?> = repository.todasLasCandidatas
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _candidataSeleccionada = MutableStateFlow<Candidata?>(null)
    val candidataSeleccionada: StateFlow<Candidata?> = _candidataSeleccionada.asStateFlow()

    private val _evaluacionCandidata = MutableStateFlow<Evaluacion?>(null)
    val evaluacionCandidata: StateFlow<Evaluacion?> = _evaluacionCandidata.asStateFlow()

    fun cargarCandidata(id: Long) {
        viewModelScope.launch {
            _candidataSeleccionada.value = repository.obtenerPorId(id)
            _evaluacionCandidata.value = repository.obtenerEvaluacion(id)
        }
    }

    fun adjuntarCv(nombreArchivo: String, uriArchivo: String) {
        val candidataActual = _candidataSeleccionada.value ?: return
        val candidataConCv = candidataActual.copy(
            cvNombre = nombreArchivo,
            cvUri = uriArchivo
        )
        viewModelScope.launch {
            repository.actualizar(candidataConCv)
            _candidataSeleccionada.value = candidataConCv
        }
    }

    private val _formulario = MutableStateFlow(FormularioRegistroState())
    val formulario: StateFlow<FormularioRegistroState> = _formulario.asStateFlow()

    fun onNombreChange(nuevoNombre: String) {
        _formulario.value = _formulario.value.copy(
            nombre = nuevoNombre,
            nombreError = null
        )
    }

    fun onAniosExperienciaChange(nuevosAnios: String) {
        _formulario.value = _formulario.value.copy(
            aniosExperiencia = nuevosAnios,
            aniosExperienciaError = null
        )
    }

    fun onPerfilUrlChange(nuevaUrl: String) {
        _formulario.value = _formulario.value.copy(
            perfilUrl = nuevaUrl,
            perfilUrlError = null
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

        _formulario.value = estadoActual.copy(guardando = true)

        viewModelScope.launch {
            try {
                val nuevaCandidata = Candidata(
                    nombre = estadoActual.nombre.trim(),
                    aniosExperiencia = expInt ?: 0,
                    perfilUrl = estadoActual.perfilUrl.trim(),
                    estado = "Pendiente"
                )
                repository.insertar(nuevaCandidata)
                _formulario.value = FormularioRegistroState(guardadoExitoso = true, guardando = false)
            } catch (e: Exception) {
                _formulario.value = _formulario.value.copy(guardando = false)
            }
        }

        return true
    }

    fun reiniciarEstadoGuardado() {
        _formulario.value = _formulario.value.copy(guardadoExitoso = false)
    }
}
