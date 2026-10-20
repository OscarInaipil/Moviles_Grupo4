package com.example.dymcyber.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dymcyber.model.Candidata
import com.example.dymcyber.repository.AppDatabase
import com.example.dymcyber.repository.CandidataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Estado que representa los valores y mensajes de error del formulario de registro.
 */
data class FormularioRegistroState(
    val nombre: String = "",
    val nombreError: String? = null,
    val aniosExperiencia: String = "",
    val aniosExperienciaError: String? = null,
    val perfilUrl: String = "",
    val perfilUrlError: String? = null,
    val guardadoExitoso: Boolean = false
)

/**
 * ViewModel encargado de la lógica de negocio y validación desacoplada para la HU-01.
 */
class CandidataViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CandidataRepository

    init {
        val dao = AppDatabase.getDatabase(application).candidataDao()
        repository = CandidataRepository(dao)
    }

    // Lista observable de candidatas para su uso en la app
    val candidatas: StateFlow<List<Candidata>?> = repository.todasLasCandidatas
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _candidataSeleccionada = MutableStateFlow<Candidata?>(null)
    val candidataSeleccionada: StateFlow<Candidata?> = _candidataSeleccionada.asStateFlow()

    fun cargarCandidata(id: Long) {
        viewModelScope.launch {
            _candidataSeleccionada.value = repository.obtenerPorId(id)
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

    /**
     * Valida los campos en el ViewModel (desacoplado de los composables).
     * Devuelve true si la información es válida e inserta en Room; false si hay errores.
     */
    fun validarYRegistrar(): Boolean {
        val estadoActual = _formulario.value

        var errorNombre: String? = null
        var errorExperiencia: String? = null
        var errorUrl: String? = null

        // Validación: Nombre obligatorio con al menos 3 caracteres
        if (estadoActual.nombre.trim().isEmpty()) {
            errorNombre = "El nombre de la postulante es obligatorio"
        } else if (estadoActual.nombre.trim().length < 3) {
            errorNombre = "El nombre debe tener al menos 3 caracteres"
        }

        // Validación: Años de experiencia numérico >= 0
        val expInt = estadoActual.aniosExperiencia.trim().toIntOrNull()
        if (estadoActual.aniosExperiencia.trim().isEmpty()) {
            errorExperiencia = "Los años de experiencia son obligatorios"
        } else if (expInt == null || expInt < 0) {
            errorExperiencia = "Ingrese un número válido mayor o igual a 0"
        }

        // Validación: URL obligatoria con formato web válido
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

        viewModelScope.launch {
            val nuevaCandidata = Candidata(
                nombre = estadoActual.nombre.trim(),
                aniosExperiencia = expInt ?: 0,
                perfilUrl = estadoActual.perfilUrl.trim(),
                estado = "Pendiente"
            )
            repository.insertar(nuevaCandidata)
            _formulario.value = FormularioRegistroState(guardadoExitoso = true)
        }

        return true
    }

    fun reiniciarEstadoGuardado() {
        _formulario.value = _formulario.value.copy(guardadoExitoso = false)
    }
}
