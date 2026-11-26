package com.example.dymcyber.repository

import com.example.dymcyber.model.Candidata
import com.example.dymcyber.model.Evaluacion
import com.example.dymcyber.model.LoginRequest
import com.example.dymcyber.model.Usuario
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface WimApiService {

    @GET("candidatas")
    suspend fun obtenerCandidatas(): Response<List<Candidata>>

    @GET("candidatas/{id}")
    suspend fun obtenerCandidataPorId(@Path("id") id: Long): Response<Candidata>

    @POST("candidatas")
    suspend fun crearCandidata(@Body candidata: Candidata): Response<Candidata>

    @PUT("candidatas/{id}")
    suspend fun actualizarCandidata(@Path("id") id: Long, @Body candidata: Candidata): Response<Candidata>

    @DELETE("candidatas/{id}")
    suspend fun eliminarCandidata(@Path("id") id: Long): Response<Unit>

    @POST("evaluaciones")
    suspend fun crearEvaluacion(@Body evaluacion: Evaluacion): Response<Evaluacion>

    @POST("auth/login")
    suspend fun iniciarSesion(@Body datos: LoginRequest): Response<Usuario>
}
