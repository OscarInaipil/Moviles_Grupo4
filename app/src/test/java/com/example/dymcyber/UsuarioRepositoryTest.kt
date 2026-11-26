package com.example.dymcyber

import com.example.dymcyber.model.Usuario
import com.example.dymcyber.repository.UsuarioRepository
import com.example.dymcyber.repository.WimApiService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class UsuarioRepositoryTest {

    private lateinit var apiService: WimApiService
    private lateinit var repository: UsuarioRepository

    @Before
    fun configurar() {
        apiService = mockk()
        repository = UsuarioRepository(apiService)
    }

    @Test
    fun loginCorrectoRetornaElUsuario() = runTest {
        val usuario = Usuario(id = 1, nombre = "Camila Rojas", correo = "evaluadora1@wim100.cl", codigoEvaluadora = "EVA-01")
        coEvery { apiService.iniciarSesion(any()) } returns Response.success(usuario)

        val resultado = repository.iniciarSesion("evaluadora1@wim100.cl", "wim2026")

        assertEquals(usuario, resultado)
        coVerify { apiService.iniciarSesion(match { it.correo == "evaluadora1@wim100.cl" && it.contrasena == "wim2026" }) }
    }

    @Test
    fun loginRechazadoRetornaNull() = runTest {
        coEvery { apiService.iniciarSesion(any()) } returns Response.error<Usuario>(401, "".toResponseBody())

        val resultado = repository.iniciarSesion("evaluadora1@wim100.cl", "clave-mala")

        assertNull(resultado)
    }
}
