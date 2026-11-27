package com.example.dymcyber

import com.example.dymcyber.model.Candidata
import com.example.dymcyber.repository.CandidataRepository
import com.example.dymcyber.repository.WimApiService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class CandidataRepositoryTest {

    private lateinit var apiService: WimApiService
    private lateinit var repository: CandidataRepository

    private val candidataPrueba = Candidata(
        id = 1,
        nombre = "Elena Torres",
        aniosExperiencia = 8,
        perfilUrl = "https://linkedin.com/in/elena-torres-wim"
    )

    @Before
    fun configurar() {
        apiService = mockk()
        repository = CandidataRepository(apiService)
    }

    @Test
    fun obtenerTodasRetornaListaCuandoBackendRespondeBien() = runTest {
        coEvery { apiService.obtenerCandidatas() } returns Response.success(listOf(candidataPrueba))

        val resultado = repository.obtenerTodas()

        assertEquals(1, resultado.size)
        assertEquals("Elena Torres", resultado[0].nombre)
    }

    @Test(expected = IllegalStateException::class)
    fun obtenerTodasLanzaExcepcionSiBackendFalla() = runTest {
        coEvery { apiService.obtenerCandidatas() } returns Response.error<List<Candidata>>(500, "".toResponseBody())

        repository.obtenerTodas()
    }

    @Test
    fun insertarRetornaCandidataCreadaConIdDelServidor() = runTest {
        val creadaEnServidor = candidataPrueba.copy(id = 4L)
        coEvery { apiService.crearCandidata(any()) } returns Response.success(creadaEnServidor)

        val resultado = repository.insertar(candidataPrueba.copy(id = 0L))

        assertEquals(4L, resultado?.id)
    }

    @Test
    fun insertarRetornaNullSiBackendRechaza() = runTest {
        coEvery { apiService.crearCandidata(any()) } returns Response.error<Candidata>(400, "".toResponseBody())

        val resultado = repository.insertar(candidataPrueba)

        assertNull(resultado)
    }

    @Test
    fun obtenerPorIdRetornaCandidataCuandoExiste() = runTest {
        coEvery { apiService.obtenerCandidataPorId(1L) } returns Response.success(candidataPrueba)

        val resultado = repository.obtenerPorId(1L)

        assertEquals(candidataPrueba, resultado)
    }

    @Test
    fun obtenerPorIdRetornaNullCuandoNoExiste() = runTest {
        coEvery { apiService.obtenerCandidataPorId(99L) } returns Response.error<Candidata>(404, "".toResponseBody())

        val resultado = repository.obtenerPorId(99L)

        assertNull(resultado)
    }

    @Test
    fun actualizarRetornaCandidataActualizada() = runTest {
        val conCv = candidataPrueba.copy(cvNombre = "cv.pdf")
        coEvery { apiService.actualizarCandidata(1L, any()) } returns Response.success(conCv)

        val resultado = repository.actualizar(conCv)

        assertEquals("cv.pdf", resultado?.cvNombre)
    }

    @Test
    fun eliminarRetornaTrueCuandoBackendRespondeOk() = runTest {
        coEvery { apiService.eliminarCandidata(1L) } returns Response.success(Unit)

        val resultado = repository.eliminar(1L)

        assertTrue(resultado)
        coVerify { apiService.eliminarCandidata(1L) }
    }

    @Test
    fun eliminarRetornaFalseCuandoNoExisteEnBackend() = runTest {
        coEvery { apiService.eliminarCandidata(1L) } returns Response.error<Unit>(404, "".toResponseBody())

        val resultado = repository.eliminar(1L)

        assertFalse(resultado)
    }

    @Test
    fun obtenerEvaluacionRetornaNullSiNoHayConexion() = runTest {
        coEvery { apiService.obtenerEvaluacionPorCandidata(1L) } throws IOException("sin conexion")

        val resultado = repository.obtenerEvaluacion(1L)

        assertNull(resultado)
    }
}
