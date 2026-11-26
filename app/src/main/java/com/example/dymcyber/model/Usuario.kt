package com.example.dymcyber.model

data class Usuario(
    val id: Long = 0,
    val nombre: String = "",
    val correo: String = "",
    val codigoEvaluadora: String = ""
)

data class LoginRequest(
    val correo: String,
    val contrasena: String
)
