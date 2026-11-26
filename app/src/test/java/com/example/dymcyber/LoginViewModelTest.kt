package com.example.dymcyber

import com.example.dymcyber.model.Usuario
import com.example.dymcyber.repository.UsuarioRepository
import com.example.dymcyber.viewmodel.LoginViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private lateinit var repository: UsuarioRepository
    private lateinit var viewModel: LoginViewModel

    private val usuarioPrueba = Usuario(
        id = 1,
        nombre = "Camila Rojas",
        correo = "evaluadora1@wim100.cl",
        codigoEvaluadora = "EVA-01"
    )

    @Before
    fun configurar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repository = mockk()
        viewModel = LoginViewModel(repository)
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    @Test
    fun camposVaciosMuestranErroresYNoLlamanAlBackend() = runTest {
        viewModel.iniciarSesion()

        assertEquals("El correo es obligatorio", viewModel.estado.value.correoError)
        assertEquals("La contraseña es obligatoria", viewModel.estado.value.contrasenaError)
        coVerify(exactly = 0) { repository.iniciarSesion(any(), any()) }
    }

    @Test
    fun correoSinArrobaMuestraError() = runTest {
        viewModel.onCorreoChange("evaluadora1")
        viewModel.onContrasenaChange("wim2026")

        viewModel.iniciarSesion()

        assertEquals("Ingrese un correo válido", viewModel.estado.value.correoError)
    }

    @Test
    fun credencialesCorrectasGuardanElUsuario() = runTest {
        coEvery { repository.iniciarSesion("evaluadora1@wim100.cl", "wim2026") } returns usuarioPrueba
        viewModel.onCorreoChange(" Evaluadora1@wim100.cl ")
        viewModel.onContrasenaChange("wim2026")

        viewModel.iniciarSesion()

        assertEquals(usuarioPrueba, viewModel.estado.value.usuario)
        assertNull(viewModel.estado.value.mensajeError)
        assertFalse(viewModel.estado.value.cargando)
    }

    @Test
    fun credencialesIncorrectasMuestranMensaje() = runTest {
        coEvery { repository.iniciarSesion(any(), any()) } returns null
        viewModel.onCorreoChange("evaluadora1@wim100.cl")
        viewModel.onContrasenaChange("clave-mala")

        viewModel.iniciarSesion()

        assertNull(viewModel.estado.value.usuario)
        assertEquals("Correo o contraseña incorrectos", viewModel.estado.value.mensajeError)
    }

    @Test
    fun sinConexionMuestraMensajeDeServidor() = runTest {
        coEvery { repository.iniciarSesion(any(), any()) } throws IOException("sin conexion")
        viewModel.onCorreoChange("evaluadora1@wim100.cl")
        viewModel.onContrasenaChange("wim2026")

        viewModel.iniciarSesion()

        assertEquals("No se pudo conectar con el servidor", viewModel.estado.value.mensajeError)
        assertFalse(viewModel.estado.value.cargando)
    }

    @Test
    fun cerrarSesionLimpiaElEstado() = runTest {
        coEvery { repository.iniciarSesion(any(), any()) } returns usuarioPrueba
        viewModel.onCorreoChange("evaluadora1@wim100.cl")
        viewModel.onContrasenaChange("wim2026")
        viewModel.iniciarSesion()

        viewModel.cerrarSesion()

        assertNull(viewModel.estado.value.usuario)
        assertEquals("", viewModel.estado.value.correo)
    }
}
