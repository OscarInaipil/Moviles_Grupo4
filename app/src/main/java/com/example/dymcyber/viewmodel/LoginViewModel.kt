package com.example.dymcyber.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dymcyber.model.Usuario
import com.example.dymcyber.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginState(
    val correo: String = "",
    val correoError: String? = null,
    val contrasena: String = "",
    val contrasenaError: String? = null,
    val mensajeError: String? = null,
    val cargando: Boolean = false,
    val usuario: Usuario? = null
)

class LoginViewModel(
    private val repository: UsuarioRepository = UsuarioRepository()
) : ViewModel() {

    private val _estado = MutableStateFlow(LoginState())
    val estado: StateFlow<LoginState> = _estado.asStateFlow()

    fun onCorreoChange(nuevoCorreo: String) {
        _estado.value = _estado.value.copy(
            correo = nuevoCorreo,
            correoError = null,
            mensajeError = null
        )
    }

    fun onContrasenaChange(nuevaContrasena: String) {
        _estado.value = _estado.value.copy(
            contrasena = nuevaContrasena,
            contrasenaError = null,
            mensajeError = null
        )
    }

    fun iniciarSesion() {
        val actual = _estado.value

        if (actual.cargando) {
            return
        }

        var errorCorreo: String? = null
        var errorContrasena: String? = null

        if (actual.correo.trim().isEmpty()) {
            errorCorreo = "El correo es obligatorio"
        } else if (!actual.correo.contains("@") || !actual.correo.contains(".")) {
            errorCorreo = "Ingrese un correo válido"
        }

        if (actual.contrasena.isEmpty()) {
            errorContrasena = "La contraseña es obligatoria"
        }

        if (errorCorreo != null || errorContrasena != null) {
            _estado.value = actual.copy(
                correoError = errorCorreo,
                contrasenaError = errorContrasena
            )
            return
        }

        _estado.value = actual.copy(cargando = true, mensajeError = null)

        viewModelScope.launch {
            try {
                val usuario = repository.iniciarSesion(actual.correo.trim().lowercase(), actual.contrasena)
                if (usuario != null) {
                    _estado.value = _estado.value.copy(cargando = false, usuario = usuario)
                } else {
                    _estado.value = _estado.value.copy(
                        cargando = false,
                        mensajeError = "Correo o contraseña incorrectos"
                    )
                }
            } catch (e: Exception) {
                _estado.value = _estado.value.copy(
                    cargando = false,
                    mensajeError = "No se pudo conectar con el servidor"
                )
            }
        }
    }

    fun cerrarSesion() {
        _estado.value = LoginState()
    }
}
