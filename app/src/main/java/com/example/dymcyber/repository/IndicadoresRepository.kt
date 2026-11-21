package com.example.dymcyber.repository

import com.example.dymcyber.model.MindicadorRespuesta

class IndicadoresRepository(
    private val apiService: IndicadoresApiService = IndicadoresRetrofitClient.apiService
) {

    suspend fun obtenerIndicadores(): MindicadorRespuesta? {
        return try {
            val respuesta = apiService.obtenerIndicadores()
            if (respuesta.isSuccessful) {
                respuesta.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
