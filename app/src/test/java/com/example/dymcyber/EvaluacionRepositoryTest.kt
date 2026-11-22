package com.example.dymcyber

import com.example.dymcyber.model.Candidata
import com.example.dymcyber.model.Evaluacion
import com.example.dymcyber.model.ResultadoEvaluacion
import com.example.dymcyber.repository.CandidataDao
import com.example.dymcyber.repository.EvaluacionDao
import com.example.dymcyber.repository.EvaluacionRepository
import com.example.dymcyber.repository.WimApiService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class EvaluacionRepositoryTest {

    private lateinit var evaluacionDao: EvaluacionDao
    private lateinit var candidataDao: CandidataDao
    private lateinit var apiService: WimApiService
    private lateinit var repository: EvaluacionRepository

    private val candidata = Candidata(
        id = 1,
        nombre = "Camila Rojas",
        aniosExperiencia = 5,
        perfilUrl = "https://linkedin.com/in/camila-rojas"
    )

    private val evaluacion = Evaluacion(
        id = 10,
        candidataId = 1,
        evaluadoraId = "EVA-01",
        notaLiderazgo = 4,
        notaTrayectoria = 5,
        notaImpacto = 4,
        notaInnovacion = 5,
        puntajePromedio = 4.5,
        observaciones = "Excelente perfil de liderazgo y mineria"
    )

    @Before
    fun configurar() {
        evaluacionDao = mockk(relaxed = true)
        candidataDao = mockk(relaxed = true)
        apiService = mockk(relaxed = true)
        every { evaluacionDao.obtenerTodas() } returns flowOf(emptyList())
        every { evaluacionDao.obtenerResultadosConsolidados() } returns flowOf(emptyList())
        repository = EvaluacionRepository(evaluacionDao, candidataDao, apiService)
    }

    @Test
    fun guardarEvaluacionInsertaEnLocalYEnviaAlBackend() = runTest {
        coEvery { evaluacionDao.existeEvaluacionParaCandidata(1L) } returns false
        coEvery { evaluacionDao.insertarEvaluacion(evaluacion) } returns 10L
        coEvery { candidataDao.actualizarCandidata(any()) } returns 1
        coEvery { apiService.crearEvaluacion(evaluacion) } returns Response.success(evaluacion)

        val resultado = repository.guardarEvaluacion(evaluacion, candidata)

        assertTrue(resultado)
        coVerify { evaluacionDao.insertarEvaluacion(evaluacion) }
        coVerify { candidataDao.actualizarCandidata(match { it.estado == "Evaluada" }) }
        coVerify { apiService.crearEvaluacion(evaluacion) }
    }

    @Test
    fun guardarEvaluacionRetornaFalseSiYaExisteEvaluacionParaLaCandidata() = runTest {
        coEvery { evaluacionDao.existeEvaluacionParaCandidata(1L) } returns true

        val resultado = repository.guardarEvaluacion(evaluacion, candidata)

        assertFalse(resultado)
        coVerify(exactly = 0) { evaluacionDao.insertarEvaluacion(any()) }
        coVerify(exactly = 0) { apiService.crearEvaluacion(any()) }
    }

    @Test
    fun guardarEvaluacionRetornaTrueAunqueFalleElBackend() = runTest {
        coEvery { evaluacionDao.existeEvaluacionParaCandidata(1L) } returns false
        coEvery { evaluacionDao.insertarEvaluacion(evaluacion) } returns 10L
        coEvery { candidataDao.actualizarCandidata(any()) } returns 1
        coEvery { apiService.crearEvaluacion(evaluacion) } throws IOException("Fallo de red")

        val resultado = repository.guardarEvaluacion(evaluacion, candidata)

        assertTrue(resultado)
        coVerify { evaluacionDao.insertarEvaluacion(evaluacion) }
        coVerify { candidataDao.actualizarCandidata(match { it.estado == "Evaluada" }) }
    }

    @Test
    fun guardarEvaluacionRetornaFalseSiFallaLaBaseLocal() = runTest {
        coEvery { evaluacionDao.existeEvaluacionParaCandidata(1L) } returns false
        coEvery { evaluacionDao.insertarEvaluacion(evaluacion) } throws RuntimeException("Error SQLite")

        val resultado = repository.guardarEvaluacion(evaluacion, candidata)

        assertFalse(resultado)
    }

    @Test
    fun obtenerPorCandidataIdConsultaElDao() = runTest {
        coEvery { evaluacionDao.obtenerPorCandidataId(1L) } returns evaluacion

        val resultado = repository.obtenerPorCandidataId(1L)

        assertNotNull(resultado)
        assertEquals(evaluacion.evaluadoraId, resultado?.evaluadoraId)
    }

    @Test
    fun yaEstaEvaluadaConsultaAlDao() = runTest {
        coEvery { evaluacionDao.existeEvaluacionParaCandidata(1L) } returns true

        val resultado = repository.yaEstaEvaluada(1L)

        assertTrue(resultado)
    }

    @Test
    fun resultadosConsolidadosRetornaFlujoDesdeDao() = runTest {
        val resumen = ResultadoEvaluacion(
            candidataId = 1,
            nombre = "Camila Rojas",
            evaluadoraId = "EVA-01",
            notaLiderazgo = 4,
            notaTrayectoria = 5,
            notaImpacto = 4,
            notaInnovacion = 5,
            puntajePromedio = 4.5,
            observaciones = "Destacada",
            fechaEvaluacion = 1000L
        )
        every { evaluacionDao.obtenerResultadosConsolidados() } returns flowOf(listOf(resumen))
        val repo = EvaluacionRepository(evaluacionDao, candidataDao, apiService)

        val lista = repo.resultadosConsolidados.first()

        assertEquals(1, lista.size)
        assertEquals("Camila Rojas", lista[0].nombre)
        assertEquals(4.5, lista[0].puntajePromedio, 0.01)
    }
}
