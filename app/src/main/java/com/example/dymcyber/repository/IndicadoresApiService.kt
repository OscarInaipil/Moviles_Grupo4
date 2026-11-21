package com.example.dymcyber.repository

import com.example.dymcyber.model.MindicadorRespuesta
import retrofit2.Response
import retrofit2.http.GET

interface IndicadoresApiService {

    @GET("api")
    suspend fun obtenerIndicadores(): Response<MindicadorRespuesta>
}
