package com.example.dymcyber.repository

import com.example.dymcyber.model.LoginRequest
import com.example.dymcyber.model.Usuario

class UsuarioRepository(
    private val apiService: WimApiService = RetrofitClient.wimApiService
) {

    suspend fun iniciarSesion(correo: String, contrasena: String): Usuario? {
        val respuesta = apiService.iniciarSesion(LoginRequest(correo, contrasena))
        return if (respuesta.isSuccessful) {
            respuesta.body()
        } else {
            null
        }
    }
}
