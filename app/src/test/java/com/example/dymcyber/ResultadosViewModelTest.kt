package com.example.dymcyber

import com.example.dymcyber.model.ResultadoEvaluacion
import com.example.dymcyber.repository.EvaluacionRepository
import com.example.dymcyber.viewmodel.ResultadosViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class ResultadosViewModelTest {

    private lateinit var repository: EvaluacionRepository
    private lateinit var viewModel: ResultadosViewModel

    @Before
    fun configurar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repository = mockk()
        viewModel = ResultadosViewModel(repository)
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    @Test
    fun cargarResultadosActualizaLaListaDelServidor() = runTest {
        val lista = listOf(
            ResultadoEvaluacion(
                candidataId = 1,
                nombre = "Candidata 1",
                evaluadoraId = "EVA-1",
                notaLiderazgo = 4,
                notaTrayectoria = 4,
                notaImpacto = 4,
                notaInnovacion = 4,
                puntajePromedio = 4.0,
                observaciones = "Observacion 1",
                fechaEvaluacion = 1000L
            )
        )
        coEvery { repository.obtenerResultadosConsolidados() } returns lista

        viewModel.cargarResultados()

        assertEquals(1, viewModel.resultados.value?.size)
        assertNull(viewModel.error.value)
    }

    @Test
    fun cargarResultadosMuestraErrorSiFallaElServidor() = runTest {
        coEvery { repository.obtenerResultadosConsolidados() } throws IOException("sin conexion")

        viewModel.cargarResultados()

        assertEquals("No se pudieron cargar los resultados desde el servidor", viewModel.error.value)
        assertTrue(viewModel.resultados.value?.isEmpty() == true)
    }

    @Test
    fun calcularPromedioGeneralConListaVaciaRetornaCero() {
        val promedio = viewModel.calcularPromedioGeneral(emptyList())
        assertEquals(0.0, promedio, 0.01)
    }

    @Test
    fun calcularPromedioGeneralCalculaPromedioCorrecto() {
        val lista = listOf(
            ResultadoEvaluacion(
                candidataId = 1,
                nombre = "Candidata 1",
                evaluadoraId = "EVA-1",
                notaLiderazgo = 4,
                notaTrayectoria = 4,
                notaImpacto = 4,
                notaInnovacion = 4,
                puntajePromedio = 4.0,
                observaciones = "Observacion 1",
                fechaEvaluacion = 1000L
            ),
            ResultadoEvaluacion(
                candidataId = 2,
                nombre = "Candidata 2",
                evaluadoraId = "EVA-2",
                notaLiderazgo = 5,
                notaTrayectoria = 5,
                notaImpacto = 5,
                notaInnovacion = 5,
                puntajePromedio = 5.0,
                observaciones = "Observacion 2",
                fechaEvaluacion = 2000L
            )
        )

        val promedio = viewModel.calcularPromedioGeneral(lista)
        assertEquals(4.5, promedio, 0.01)
    }
}
