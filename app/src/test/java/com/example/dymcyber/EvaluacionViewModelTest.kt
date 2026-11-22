package com.example.dymcyber

import android.app.Application
import com.example.dymcyber.model.Candidata
import com.example.dymcyber.model.Evaluacion
import com.example.dymcyber.repository.CandidataRepository
import com.example.dymcyber.repository.EvaluacionRepository
import com.example.dymcyber.viewmodel.EvaluacionViewModel
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EvaluacionViewModelTest {

    private lateinit var application: Application
    private lateinit var evaluacionRepository: EvaluacionRepository
    private lateinit var candidataRepository: CandidataRepository
    private lateinit var viewModel: EvaluacionViewModel

    private val candidata = Candidata(
        id = 2,
        nombre = "Sofia Valenzuela",
        aniosExperiencia = 10,
        perfilUrl = "https://linkedin.com/in/sofia-valenzuela"
    )

    private val evaluacionExistente = Evaluacion(
        id = 20,
        candidataId = 2,
        evaluadoraId = "EVA-02",
        notaLiderazgo = 5,
        notaTrayectoria = 5,
        notaImpacto = 5,
        notaInnovacion = 5,
        puntajePromedio = 5.0,
        observaciones = "Liderazgo demostrado en operaciones"
    )

    @Before
    fun configurar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        application = mockk(relaxed = true)
        evaluacionRepository = mockk(relaxed = true)
        candidataRepository = mockk(relaxed = true)
        viewModel = EvaluacionViewModel(application, evaluacionRepository, candidataRepository)
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    @Test
    fun cargarCandidataSinEvaluacionDejaElEstadoListoParaEvaluar() = runTest {
        coEvery { candidataRepository.obtenerPorId(2L) } returns candidata
        coEvery { evaluacionRepository.obtenerPorCandidataId(2L) } returns null

        viewModel.cargarCandidata(2L)

        val estado = viewModel.estado.value
        assertFalse(estado.cargando)
        assertFalse(estado.yaEvaluada)
        assertEquals(candidata, estado.candidata)
    }

    @Test
    fun cargarCandidataConEvaluacionPreviaCargaLasNotasYBloquea() = runTest {
        coEvery { candidataRepository.obtenerPorId(2L) } returns candidata
        coEvery { evaluacionRepository.obtenerPorCandidataId(2L) } returns evaluacionExistente

        viewModel.cargarCandidata(2L)

        val estado = viewModel.estado.value
        assertFalse(estado.cargando)
        assertTrue(estado.yaEvaluada)
        assertEquals(5.0, estado.puntajePromedio, 0.01)
        assertEquals("EVA-02", estado.evaluadoraId)
    }

    @Test
    fun setNotasRecalculaElPromedioCorrectamente() {
        viewModel.setNotaLiderazgo(5)
        viewModel.setNotaTrayectoria(5)
        viewModel.setNotaImpacto(4)
        viewModel.setNotaInnovacion(4)

        assertEquals(4.5, viewModel.estado.value.puntajePromedio, 0.01)
    }

    @Test
    fun validarYGuardarRetornaFalseSiCamposEstanVacios() {
        coEvery { candidataRepository.obtenerPorId(2L) } returns candidata
        coEvery { evaluacionRepository.obtenerPorCandidataId(2L) } returns null
        viewModel.cargarCandidata(2L)

        viewModel.onEvaluadoraIdChange("")
        viewModel.onObservacionesChange("")

        val resultado = viewModel.validarYGuardarEvaluacion()

        assertFalse(resultado)
        assertNotNull(viewModel.estado.value.evaluadoraIdError)
        assertNotNull(viewModel.estado.value.observacionesError)
    }

    @Test
    fun validarYGuardarGuardaExitosamenteConDatosValidos() = runTest {
        coEvery { candidataRepository.obtenerPorId(2L) } returns candidata
        coEvery { evaluacionRepository.obtenerPorCandidataId(2L) } returns null
        coEvery { evaluacionRepository.guardarEvaluacion(any(), any()) } returns true
        viewModel.cargarCandidata(2L)

        viewModel.onEvaluadoraIdChange("EVA-09")
        viewModel.onObservacionesChange("Excelente perfil con amplia experiencia")

        val resultado = viewModel.validarYGuardarEvaluacion()

        assertTrue(resultado)
        assertTrue(viewModel.estado.value.guardadoExitoso)
        assertTrue(viewModel.estado.value.yaEvaluada)
        coVerify { evaluacionRepository.guardarEvaluacion(any(), any()) }
    }
}
