package com.example.dymcyber

import com.example.dymcyber.model.Candidata
import com.example.dymcyber.model.Evaluacion
import com.example.dymcyber.repository.EvaluacionRepository
import com.example.dymcyber.repository.WimApiService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class EvaluacionRepositoryTest {

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
        apiService = mockk()
        repository = EvaluacionRepository(apiService)
    }

    @Test
    fun guardarEvaluacionRetornaEvaluacionCuandoBackendRespondeBien() = runTest {
        coEvery { apiService.crearEvaluacion(evaluacion) } returns Response.success(evaluacion)

        val resultado = repository.guardarEvaluacion(evaluacion)

        assertNotNull(resultado)
        assertEquals(4.5, resultado?.puntajePromedio ?: 0.0, 0.01)
        coVerify { apiService.crearEvaluacion(evaluacion) }
    }

    @Test
    fun guardarEvaluacionRetornaNullSiBackendRespondeConflicto() = runTest {
        coEvery { apiService.crearEvaluacion(evaluacion) } returns Response.error<Evaluacion>(409, "".toResponseBody())

        val resultado = repository.guardarEvaluacion(evaluacion)

        assertNull(resultado)
    }

    @Test
    fun guardarEvaluacionRetornaNullSiNoHayConexion() = runTest {
        coEvery { apiService.crearEvaluacion(evaluacion) } throws IOException("Fallo de red")

        val resultado = repository.guardarEvaluacion(evaluacion)

        assertNull(resultado)
    }

    @Test
    fun obtenerPorCandidataIdRetornaEvaluacionCuandoExiste() = runTest {
        coEvery { apiService.obtenerEvaluacionPorCandidata(1L) } returns Response.success(evaluacion)

        val resultado = repository.obtenerPorCandidataId(1L)

        assertNotNull(resultado)
        assertEquals("EVA-01", resultado?.evaluadoraId)
    }

    @Test
    fun obtenerPorCandidataIdRetornaNullCuandoNoExiste() = runTest {
        coEvery { apiService.obtenerEvaluacionPorCandidata(1L) } returns Response.error<Evaluacion>(404, "".toResponseBody())

        val resultado = repository.obtenerPorCandidataId(1L)

        assertNull(resultado)
    }

    @Test
    fun obtenerResultadosConsolidadosCombinaEvaluacionesYCandidatas() = runTest {
        coEvery { apiService.obtenerEvaluaciones() } returns Response.success(listOf(evaluacion))
        coEvery { apiService.obtenerCandidatas() } returns Response.success(listOf(candidata))

        val lista = repository.obtenerResultadosConsolidados()

        assertEquals(1, lista.size)
        assertEquals("Camila Rojas", lista[0].nombre)
        assertEquals(4.5, lista[0].puntajePromedio, 0.01)
    }

    @Test(expected = IllegalStateException::class)
    fun obtenerResultadosConsolidadosLanzaExcepcionSiFallaElBackend() = runTest {
        coEvery { apiService.obtenerEvaluaciones() } returns Response.error<List<Evaluacion>>(500, "".toResponseBody())
        coEvery { apiService.obtenerCandidatas() } returns Response.success(listOf(candidata))

        repository.obtenerResultadosConsolidados()
    }
}
