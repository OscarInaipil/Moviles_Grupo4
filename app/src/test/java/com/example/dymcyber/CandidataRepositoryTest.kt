package com.example.dymcyber

import com.example.dymcyber.model.Candidata
import com.example.dymcyber.repository.CandidataDao
import com.example.dymcyber.repository.CandidataRepository
import com.example.dymcyber.repository.WimApiService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class CandidataRepositoryTest {

    private lateinit var candidataDao: CandidataDao
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
        candidataDao = mockk(relaxed = true)
        apiService = mockk()
        every { candidataDao.obtenerTodas() } returns flowOf(emptyList())
        repository = CandidataRepository(candidataDao, null, apiService)
    }

    @Test
    fun insertarGuardaEnLocalYEnviaAlBackendConElIdLocal() = runTest {
        coEvery { candidataDao.insertarCandidata(any()) } returns 5L
        coEvery { apiService.crearCandidata(any()) } returns Response.success(candidataPrueba)

        val id = repository.insertar(candidataPrueba.copy(id = 0))

        assertEquals(5L, id)
        coVerify { apiService.crearCandidata(match { it.id == 5L }) }
    }

    @Test
    fun insertarRetornaElIdLocalAunqueFalleElBackend() = runTest {
        coEvery { candidataDao.insertarCandidata(any()) } returns 7L
        coEvery { apiService.crearCandidata(any()) } throws IOException("sin conexion")

        val id = repository.insertar(candidataPrueba.copy(id = 0))

        assertEquals(7L, id)
    }

    @Test
    fun sincronizarInsertaLasCandidatasNuevasDelBackend() = runTest {
        coEvery { apiService.obtenerCandidatas() } returns Response.success(listOf(candidataPrueba))
        coEvery { candidataDao.obtenerPorId(1L) } returns null

        repository.sincronizarConBackend()

        coVerify { candidataDao.insertarCandidata(candidataPrueba) }
    }

    @Test
    fun sincronizarActualizaLasCandidatasQueYaExisten() = runTest {
        coEvery { apiService.obtenerCandidatas() } returns Response.success(listOf(candidataPrueba))
        coEvery { candidataDao.obtenerPorId(1L) } returns candidataPrueba

        repository.sincronizarConBackend()

        coVerify { candidataDao.actualizarCandidata(candidataPrueba) }
    }

    @Test
    fun sincronizarSinConexionNoModificaLaBaseLocal() = runTest {
        coEvery { apiService.obtenerCandidatas() } throws IOException("sin conexion")

        repository.sincronizarConBackend()

        coVerify(exactly = 0) { candidataDao.insertarCandidata(any()) }
        coVerify(exactly = 0) { candidataDao.actualizarCandidata(any()) }
    }

    @Test
    fun obtenerPorIdUsaLosDatosDelBackendCuandoResponde() = runTest {
        val remota = candidataPrueba.copy(estado = "Evaluada")
        coEvery { apiService.obtenerCandidataPorId(1L) } returns Response.success(remota)

        val resultado = repository.obtenerPorId(1L)

        assertEquals("Evaluada", resultado?.estado)
        coVerify { candidataDao.actualizarCandidata(remota) }
    }

    @Test
    fun obtenerPorIdUsaLaBaseLocalSiFallaElBackend() = runTest {
        coEvery { apiService.obtenerCandidataPorId(1L) } throws IOException("sin conexion")
        coEvery { candidataDao.obtenerPorId(1L) } returns candidataPrueba

        val resultado = repository.obtenerPorId(1L)

        assertEquals(candidataPrueba, resultado)
    }

    @Test
    fun actualizarModificaLaBaseLocalYElBackend() = runTest {
        coEvery { candidataDao.actualizarCandidata(any()) } returns 1
        coEvery { apiService.actualizarCandidata(1L, any()) } returns Response.success(candidataPrueba)

        val filas = repository.actualizar(candidataPrueba)

        assertEquals(1, filas)
        coVerify { apiService.actualizarCandidata(1L, candidataPrueba) }
    }

    @Test
    fun eliminarBorraEnLaBaseLocalYEnElBackend() = runTest {
        coEvery { candidataDao.eliminarCandidata(any()) } returns 1
        coEvery { apiService.eliminarCandidata(1L) } returns Response.success(Unit)

        val filas = repository.eliminar(candidataPrueba)

        assertEquals(1, filas)
        coVerify { apiService.eliminarCandidata(1L) }
    }

    @Test
    fun obtenerEvaluacionSinDaoDeEvaluacionesRetornaNull() = runTest {
        val resultado = repository.obtenerEvaluacion(1L)

        assertNull(resultado)
    }

    @Test
    fun precargarCandidatasPruebaInsertaTresCandidatas() = runTest {
        coEvery { candidataDao.insertarCandidata(any()) } returns 1L
        coEvery { apiService.crearCandidata(any()) } returns Response.success(candidataPrueba)

        repository.precargarCandidatasPrueba()

        coVerify(exactly = 3) { candidataDao.insertarCandidata(any()) }
        coVerify(exactly = 3) { apiService.crearCandidata(any()) }
    }
}
