package com.example.dymcyber

import com.example.dymcyber.model.IndicadorItem
import com.example.dymcyber.model.MindicadorRespuesta
import com.example.dymcyber.repository.IndicadoresRepository
import com.example.dymcyber.viewmodel.IndicadoresViewModel
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

@OptIn(ExperimentalCoroutinesApi::class)
class IndicadoresViewModelTest {

    private lateinit var repository: IndicadoresRepository

    @Before
    fun configurar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repository = mockk()
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    @Test
    fun cargaLosIndicadoresAlIniciar() = runTest {
        val respuesta = MindicadorRespuesta(uf = IndicadorItem(codigo = "uf", valor = 39500.0))
        coEvery { repository.obtenerIndicadores() } returns respuesta

        val viewModel = IndicadoresViewModel(repository)

        assertEquals(respuesta, viewModel.indicadores.value)
        assertNull(viewModel.error.value)
        assertFalse(viewModel.cargando.value)
    }

    @Test
    fun muestraErrorCuandoNoLlegaRespuesta() = runTest {
        coEvery { repository.obtenerIndicadores() } returns null

        val viewModel = IndicadoresViewModel(repository)

        assertNull(viewModel.indicadores.value)
        assertEquals("No se pudieron cargar los indicadores economicos", viewModel.error.value)
        assertFalse(viewModel.cargando.value)
    }

    @Test
    fun muestraElMensajeCuandoElRepositorioLanzaExcepcion() = runTest {
        coEvery { repository.obtenerIndicadores() } throws RuntimeException("Servicio caido")

        val viewModel = IndicadoresViewModel(repository)

        assertEquals("Servicio caido", viewModel.error.value)
        assertFalse(viewModel.cargando.value)
    }

    @Test
    fun reintentarVuelveAConsultarYLimpiaElError() = runTest {
        val respuesta = MindicadorRespuesta(dolar = IndicadorItem(codigo = "dolar", valor = 950.0))
        coEvery { repository.obtenerIndicadores() } returns null andThen respuesta

        val viewModel = IndicadoresViewModel(repository)
        viewModel.cargarIndicadores()

        assertEquals(950.0, viewModel.indicadores.value?.dolar?.valor ?: 0.0, 0.001)
        assertNull(viewModel.error.value)
        coVerify(exactly = 2) { repository.obtenerIndicadores() }
    }
}
