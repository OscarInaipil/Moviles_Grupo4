package com.example.dymcyber

import com.example.dymcyber.model.Candidata
import com.example.dymcyber.repository.CandidataRepository
import com.example.dymcyber.viewmodel.CandidataViewModel
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class CandidataViewModelTest {

    private lateinit var repository: CandidataRepository
    private lateinit var viewModel: CandidataViewModel

    private val candidata = Candidata(
        id = 1L,
        nombre = "Elena Torres",
        aniosExperiencia = 8,
        perfilUrl = "https://linkedin.com/in/elena-torres-wim"
    )

    @Before
    fun configurar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repository = mockk(relaxed = true)
        viewModel = CandidataViewModel(repository)
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    @Test
    fun cargarCandidatasActualizaListaCuandoRespondeElServidor() = runTest {
        coEvery { repository.obtenerTodas() } returns listOf(candidata)

        viewModel.cargarCandidatas()

        assertEquals(1, viewModel.candidatas.value?.size)
        assertNull(viewModel.errorLista.value)
    }

    @Test
    fun cargarCandidatasGuardaErrorCuandoFallaLaConexion() = runTest {
        coEvery { repository.obtenerTodas() } throws IOException("sin conexion")

        viewModel.cargarCandidatas()

        assertEquals("No se pudo conectar con el servidor", viewModel.errorLista.value)
        assertTrue(viewModel.candidatas.value?.isEmpty() == true)
    }

    @Test
    fun validarYRegistrarConCamposVaciosMuestraErrores() {
        val resultado = viewModel.validarYRegistrar()

        assertFalse(resultado)
        assertNotNull(viewModel.formulario.value.nombreError)
        assertNotNull(viewModel.formulario.value.aniosExperienciaError)
        assertNotNull(viewModel.formulario.value.perfilUrlError)
    }

    @Test
    fun validarYRegistrarConUrlInvalidaMuestraError() {
        viewModel.onNombreChange("Elena Torres")
        viewModel.onAniosExperienciaChange("5")
        viewModel.onPerfilUrlChange("linkedin.com/in/elena")

        val resultado = viewModel.validarYRegistrar()

        assertFalse(resultado)
        assertNotNull(viewModel.formulario.value.perfilUrlError)
    }

    @Test
    fun validarYRegistrarEnviaAlBackendYRecargaLista() = runTest {
        coEvery { repository.insertar(any()) } returns candidata
        coEvery { repository.obtenerTodas() } returns listOf(candidata)
        viewModel.onNombreChange("Elena Torres")
        viewModel.onAniosExperienciaChange("8")
        viewModel.onPerfilUrlChange("https://linkedin.com/in/elena-torres-wim")

        val resultado = viewModel.validarYRegistrar()

        assertTrue(resultado)
        assertTrue(viewModel.formulario.value.guardadoExitoso)
        coVerify { repository.insertar(any()) }
        coVerify { repository.obtenerTodas() }
    }

    @Test
    fun adjuntarCvActualizaCandidataYRefrescaLista() = runTest {
        val conCv = candidata.copy(cvNombre = "cv_elena.pdf", cvUri = "content://cv/1")
        coEvery { repository.obtenerPorId(1L) } returns candidata
        coEvery { repository.obtenerEvaluacion(1L) } returns null
        coEvery { repository.actualizar(any()) } returns conCv
        coEvery { repository.obtenerTodas() } returns listOf(conCv)

        viewModel.cargarCandidata(1L)
        viewModel.adjuntarCv("cv_elena.pdf", "content://cv/1")

        assertEquals("cv_elena.pdf", viewModel.candidataSeleccionada.value?.cvNombre)
        coVerify { repository.actualizar(match { it.cvNombre == "cv_elena.pdf" }) }
    }

    @Test
    fun eliminarCandidataLlamaAlBackendYEjecutaCallback() = runTest {
        var callbackEjecutado = false
        coEvery { repository.obtenerPorId(1L) } returns candidata
        coEvery { repository.obtenerEvaluacion(1L) } returns null
        coEvery { repository.eliminar(1L) } returns true
        coEvery { repository.obtenerTodas() } returns emptyList()

        viewModel.cargarCandidata(1L)
        viewModel.eliminarCandidata { callbackEjecutado = true }

        assertTrue(callbackEjecutado)
        assertNull(viewModel.candidataSeleccionada.value)
        coVerify { repository.eliminar(1L) }
    }
}
