package com.example.dymcyber.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dymcyber.viewmodel.CandidataViewModel

/**
 * Pantalla de registro de candidatas para el flujo inicial de WIM 100 (HU-01).
 * Implementa validación visual con íconos de error desacoplada en el ViewModel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroCandidataScreen(
    viewModel: CandidataViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val formulario by viewModel.formulario.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "WIM 100 · Registro Postulante",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Ingresa los antecedentes de la postulante. Toda la información ingresada debe ser simulada o ficticia.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(14.dp)
                )
            }

            // Campo: Nombre de la candidata
            OutlinedTextField(
                value = formulario.nombre,
                onValueChange = viewModel::onNombreChange,
                label = { Text("Nombre ficticio de la candidata") },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                },
                trailingIcon = {
                    if (formulario.nombreError != null) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error de validación",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                isError = formulario.nombreError != null,
                supportingText = {
                    formulario.nombreError?.let { mensaje ->
                        Text(text = mensaje, color = MaterialTheme.colorScheme.error)
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Campo: Años de experiencia
            OutlinedTextField(
                value = formulario.aniosExperiencia,
                onValueChange = viewModel::onAniosExperienciaChange,
                label = { Text("Años de experiencia laboral") },
                leadingIcon = {
                    Icon(Icons.Default.Work, contentDescription = null)
                },
                trailingIcon = {
                    if (formulario.aniosExperienciaError != null) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error de validación",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                isError = formulario.aniosExperienciaError != null,
                supportingText = {
                    formulario.aniosExperienciaError?.let { mensaje ->
                        Text(text = mensaje, color = MaterialTheme.colorScheme.error)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Campo: URL de perfil simulado
            OutlinedTextField(
                value = formulario.perfilUrl,
                onValueChange = viewModel::onPerfilUrlChange,
                label = { Text("Enlace a perfil profesional simulado") },
                placeholder = { Text("https://linkedin.com/in/perfil-simulado") },
                leadingIcon = {
                    Icon(Icons.Default.Link, contentDescription = null)
                },
                trailingIcon = {
                    if (formulario.perfilUrlError != null) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error de validación",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                isError = formulario.perfilUrlError != null,
                supportingText = {
                    formulario.perfilUrlError?.let { mensaje ->
                        Text(text = mensaje, color = MaterialTheme.colorScheme.error)
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Botón de guardar
            Button(
                onClick = { viewModel.validarYRegistrar() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = "Registrar Candidata",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }

    // Diálogo de confirmación al guardar en Room
    if (formulario.guardadoExitoso) {
        AlertDialog(
            onDismissRequest = { viewModel.reiniciarEstadoGuardado() },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = {
                Text(text = "¡Registro Exitoso!")
            },
            text = {
                Text(text = "La postulación ha sido guardada en la base de datos local (Room) con estado 'Pendiente de evaluación'.")
            },
            confirmButton = {
                TextButton(onClick = { viewModel.reiniciarEstadoGuardado() }) {
                    Text("Aceptar")
                }
            }
        )
    }
}
