package com.example.dymcyber

import android.app.Application
import com.example.dymcyber.model.ResultadoEvaluacion
import com.example.dymcyber.repository.EvaluacionRepository
import com.example.dymcyber.viewmodel.ResultadosViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ResultadosViewModelTest {

    private lateinit var application: Application
    private lateinit var repository: EvaluacionRepository
    private lateinit var viewModel: ResultadosViewModel

    @Before
    fun configurar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        application = mockk(relaxed = true)
        repository = mockk()
        every { repository.resultadosConsolidados } returns flowOf(emptyList())
        viewModel = ResultadosViewModel(application, repository)
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
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
