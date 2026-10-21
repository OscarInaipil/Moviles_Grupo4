package com.example.dymcyber.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dymcyber.viewmodel.EvaluacionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatrizEvaluacionScreen(
    candidataId: Long,
    alVolver: () -> Unit,
    viewModel: EvaluacionViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val estado by viewModel.estado.collectAsState()

    LaunchedEffect(candidataId) {
        viewModel.cargarCandidata(candidataId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Matriz de Evaluación",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = alVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        if (estado.cargando) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                estado.candidata?.let { candidata ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Evaluando a:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = candidata.nombre,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${candidata.aniosExperiencia} años de experiencia laboral",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                if (estado.yaEvaluada && !estado.guardadoExitoso) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Esta postulante ya cuenta con una evaluación registrada.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }

                Text(
                    text = "Criterios de Evaluación WIM 100",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                CriterioItem(
                    titulo = "1. Liderazgo y Gestión",
                    descripcion = "Capacidad de guiar equipos y encabezar iniciativas en el rubro minero.",
                    notaSeleccionada = estado.notaLiderazgo,
                    habilitado = !estado.yaEvaluada,
                    alCambiarNota = viewModel::setNotaLiderazgo
                )

                CriterioItem(
                    titulo = "2. Trayectoria y Experiencia",
                    descripcion = "Solidez, consistencia y logros alcanzados en su carrera profesional.",
                    notaSeleccionada = estado.notaTrayectoria,
                    habilitado = !estado.yaEvaluada,
                    alCambiarNota = viewModel::setNotaTrayectoria
                )

                CriterioItem(
                    titulo = "3. Impacto en el Ecosistema",
                    descripcion = "Contribución visible a la inclusión femenina y desarrollo comunitario.",
                    notaSeleccionada = estado.notaImpacto,
                    habilitado = !estado.yaEvaluada,
                    alCambiarNota = viewModel::setNotaImpacto
                )

                CriterioItem(
                    titulo = "4. Innovación y Soluciones",
                    descripcion = "Implementación de ideas, tecnología y mejoras en procesos sostenibles.",
                    notaSeleccionada = estado.notaInnovacion,
                    habilitado = !estado.yaEvaluada,
                    alCambiarNota = viewModel::setNotaInnovacion
                )

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Puntaje Promedio Final",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Escala de 1.0 a 5.0",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            AnimatedContent(
                                targetState = estado.puntajePromedio,
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                                label = "animacionPromedio"
                            ) { promedio ->
                                Text(
                                    text = String.format("%.1f", promedio),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "Trazabilidad y Justificación",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = estado.evaluadoraId,
                    onValueChange = viewModel::onEvaluadoraIdChange,
                    label = { Text("Identificador de la evaluadora") },
                    placeholder = { Text("Ej: EVA-01") },
                    leadingIcon = {
                        Icon(Icons.Default.Badge, contentDescription = null)
                    },
                    trailingIcon = {
                        if (estado.evaluadoraIdError != null) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Error",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    isError = estado.evaluadoraIdError != null,
                    supportingText = {
                        estado.evaluadoraIdError?.let { mensaje ->
                            Text(text = mensaje, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    enabled = !estado.yaEvaluada,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = estado.observaciones,
                    onValueChange = viewModel::onObservacionesChange,
                    label = { Text("Observaciones / Justificación") },
                    placeholder = { Text("Ingresa los motivos de las calificaciones otorgadas...") },
                    leadingIcon = {
                        Icon(Icons.AutoMirrored.Filled.Comment, contentDescription = null)
                    },
                    trailingIcon = {
                        if (estado.observacionesError != null) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Error",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    isError = estado.observacionesError != null,
                    supportingText = {
                        estado.observacionesError?.let { mensaje ->
                            Text(text = mensaje, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    enabled = !estado.yaEvaluada,
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (!estado.yaEvaluada) {
                    Button(
                        onClick = { viewModel.validarYGuardarEvaluacion() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Text(
                            text = "Guardar Evaluación",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }

    if (estado.guardadoExitoso) {
        AlertDialog(
            onDismissRequest = {
                viewModel.reiniciarEstadoGuardado()
                alVolver()
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = {
                Text(text = "Evaluación Guardada")
            },
            text = {
                Text(
                    text = "La evaluación de ${estado.candidata?.nombre} ha sido guardada con un promedio de ${String.format("%.1f", estado.puntajePromedio)}. La postulación pasó al estado Evaluada."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.reiniciarEstadoGuardado()
                        alVolver()
                    }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }
}

@Composable
fun CriterioItem(
    titulo: String,
    descripcion: String,
    notaSeleccionada: Int,
    habilitado: Boolean,
    alCambiarNota: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(
                        text = "$notaSeleccionada / 5",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                text = descripcion,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                (1..5).forEach { valor ->
                    FilterChip(
                        selected = notaSeleccionada == valor,
                        onClick = {
                            if (habilitado) {
                                alCambiarNota(valor)
                            }
                        },
                        label = { Text(text = "$valor") },
                        enabled = habilitado,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
        }
    }
}
