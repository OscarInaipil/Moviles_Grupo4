package com.example.dymcyber.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dymcyber.model.Candidata
import com.example.dymcyber.model.MindicadorRespuesta
import com.example.dymcyber.viewmodel.CandidataViewModel
import com.example.dymcyber.viewmodel.IndicadoresViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListadoCandidatasScreen(
    viewModel: CandidataViewModel,
    indicadoresViewModel: IndicadoresViewModel = viewModel(),
    alSeleccionarCandidata: (Long) -> Unit,
    alAgregarCandidata: () -> Unit,
    alVerResultados: () -> Unit,
    alCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val candidatas = viewModel.candidatas.collectAsState().value
    val errorLista = viewModel.errorLista.collectAsState().value
    val indicadores = indicadoresViewModel.indicadores.collectAsState().value
    val cargandoIndicadores = indicadoresViewModel.cargando.collectAsState().value
    val errorIndicadores = indicadoresViewModel.error.collectAsState().value

    val estadoPantalla = when {
        candidatas == null -> "cargando"
        errorLista != null && candidatas.isEmpty() -> "error"
        candidatas.isEmpty() -> "vacio"
        else -> "lista"
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "WIM 100 · Candidaturas",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = alVerResultados) {
                        Icon(
                            imageVector = Icons.Default.Assessment,
                            contentDescription = "Ver resultados consolidados"
                        )
                    }
                    IconButton(onClick = alCerrarSesion) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar sesión"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = alAgregarCandidata,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Nueva candidata") }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TarjetaIndicadores(
                indicadores = indicadores,
                cargando = cargandoIndicadores,
                error = errorIndicadores,
                alReintentar = { indicadoresViewModel.cargarIndicadores() },
                modifier = Modifier.padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 4.dp)
            )

            Crossfade(
                targetState = estadoPantalla,
                animationSpec = tween(400),
                label = "estadoListado",
                modifier = Modifier.fillMaxSize()
            ) { estado ->
                when (estado) {
                    "cargando" -> IndicadorCarga()
                    "error" -> MensajeErrorServidor(
                        mensaje = errorLista ?: "No se pudo conectar con el servidor",
                        alReintentar = { viewModel.cargarCandidatas() }
                    )
                    "vacio" -> MensajeSinCandidatas(alCargarPrueba = { viewModel.cargarCandidatasPrueba() })
                    else -> ListaCandidatas(
                        candidatas = candidatas.orEmpty(),
                        alSeleccionarCandidata = alSeleccionarCandidata
                    )
                }
            }
        }
    }
}

@Composable
fun TarjetaIndicadores(
    indicadores: MindicadorRespuesta?,
    cargando: Boolean,
    error: String?,
    alReintentar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Indicadores Minería (Chile)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (cargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    IconButton(
                        onClick = alReintentar,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Actualizar indicadores",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (cargando && indicadores == null) {
                Text(
                    text = "Consultando servicio de indicadores...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else if (error != null && indicadores == null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "No se pudieron obtener indicadores en vivo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    OutlinedButton(
                        onClick = alReintentar,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("Reintentar", style = MaterialTheme.typography.labelSmall)
                    }
                }
            } else if (indicadores != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    IndicadorItemVista(
                        titulo = "Cobre",
                        valor = indicadores.libraCobre?.valor?.let { "US$ $it" } ?: "-",
                        subtitulo = "USD/lb"
                    )
                    IndicadorItemVista(
                        titulo = "Dólar",
                        valor = indicadores.dolar?.valor?.let { "$$it" } ?: "-",
                        subtitulo = "CLP"
                    )
                    IndicadorItemVista(
                        titulo = "UF",
                        valor = indicadores.uf?.valor?.let { "$$it" } ?: "-",
                        subtitulo = "CLP"
                    )
                }
            }
        }
    }
}

@Composable
fun IndicadorItemVista(
    titulo: String,
    valor: String,
    subtitulo: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = subtitulo,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
fun ListaCandidatas(
    candidatas: List<Candidata>,
    alSeleccionarCandidata: (Long) -> Unit
) {
    val pendientes = candidatas.count { it.estado == "Pendiente" }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Registradas: ${candidatas.size} · Pendientes de evaluación: $pendientes",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        items(candidatas, key = { it.id }) { candidata ->
            TarjetaCandidata(
                candidata = candidata,
                alPresionar = { alSeleccionarCandidata(candidata.id) },
                modifier = Modifier.animateItem()
            )
        }
    }
}

@Composable
fun TarjetaCandidata(
    candidata: Candidata,
    alPresionar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val textoExperiencia = if (candidata.aniosExperiencia == 1) {
        "1 año de experiencia"
    } else {
        "${candidata.aniosExperiencia} años de experiencia"
    }

    Card(
        onClick = alPresionar,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = candidata.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = textoExperiencia,
                    style = MaterialTheme.typography.bodyMedium
                )
                EtiquetaEstado(estado = candidata.estado)
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null
            )
        }
    }
}

@Composable
fun EtiquetaEstado(estado: String) {
    val estaEvaluada = estado == "Evaluada"
    val colorFondo = if (estaEvaluada) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.tertiaryContainer
    }
    val colorTexto = if (estaEvaluada) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onTertiaryContainer
    }

    Surface(
        color = colorFondo,
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = estado,
            color = colorTexto,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun IndicadorCarga() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun MensajeSinCandidatas(alCargarPrueba: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Aún no hay candidatas registradas",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Presiona \"Nueva candidata\" para registrar la primera postulación.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedButton(onClick = alCargarPrueba) {
            Text("Cargar candidatas de prueba")
        }
    }
}

@Composable
fun MensajeErrorServidor(
    mensaje: String,
    alReintentar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = mensaje,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onClick = alReintentar) {
            Text("Reintentar conexión")
        }
    }
}
