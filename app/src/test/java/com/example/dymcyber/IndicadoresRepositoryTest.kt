package com.example.dymcyber

import com.example.dymcyber.model.IndicadorItem
import com.example.dymcyber.model.MindicadorRespuesta
import com.example.dymcyber.repository.IndicadoresApiService
import com.example.dymcyber.repository.IndicadoresRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class IndicadoresRepositoryTest {

    private lateinit var apiService: IndicadoresApiService
    private lateinit var repository: IndicadoresRepository

    @Before
    fun configurar() {
        apiService = mockk()
        repository = IndicadoresRepository(apiService)
    }

    @Test
    fun retornaLosIndicadoresCuandoLaApiRespondeBien() = runTest {
        val respuesta = MindicadorRespuesta(
            libraCobre = IndicadorItem(codigo = "libra_cobre", valor = 4.25),
            dolar = IndicadorItem(codigo = "dolar", valor = 950.5)
        )
        coEvery { apiService.obtenerIndicadores() } returns Response.success(respuesta)

        val resultado = repository.obtenerIndicadores()

        assertEquals(4.25, resultado?.libraCobre?.valor ?: 0.0, 0.001)
        assertEquals(950.5, resultado?.dolar?.valor ?: 0.0, 0.001)
    }

    @Test
    fun retornaNullCuandoLaApiRespondeConError() = runTest {
        coEvery { apiService.obtenerIndicadores() } returns Response.error<MindicadorRespuesta>(500, "".toResponseBody())

        val resultado = repository.obtenerIndicadores()

        assertNull(resultado)
    }

    @Test
    fun retornaNullCuandoNoHayConexion() = runTest {
        coEvery { apiService.obtenerIndicadores() } throws IOException("sin conexion")

        val resultado = repository.obtenerIndicadores()

        assertNull(resultado)
    }
}
