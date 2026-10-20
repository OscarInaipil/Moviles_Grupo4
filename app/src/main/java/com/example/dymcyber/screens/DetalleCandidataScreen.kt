package com.example.dymcyber.screens

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dymcyber.model.Candidata
import com.example.dymcyber.viewmodel.CandidataViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleCandidataScreen(
    candidataId: Long,
    viewModel: CandidataViewModel,
    alVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(candidataId) {
        viewModel.cargarCandidata(candidataId)
    }

    val candidata = viewModel.candidataSeleccionada.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalle de candidata",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = alVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver al listado"
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
        if (candidata == null || candidata.id != candidataId) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            ContenidoDetalle(
                candidata = candidata,
                alAdjuntarCv = { nombre, uri -> viewModel.adjuntarCv(nombre, uri) },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun ContenidoDetalle(
    candidata: Candidata,
    alAdjuntarCv: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val selectorCv = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            guardarPermisoLectura(context, uri)
            alAdjuntarCv(obtenerNombreArchivo(context, uri), uri.toString())
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = candidata.nombre,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            EtiquetaEstado(estado = candidata.estado)
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Antecedentes de la postulante",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                DatoCandidata(
                    icono = Icons.Default.Work,
                    titulo = "Experiencia laboral",
                    valor = if (candidata.aniosExperiencia == 1) "1 año" else "${candidata.aniosExperiencia} años"
                )
                DatoCandidata(
                    icono = Icons.Default.Link,
                    titulo = "Perfil profesional simulado",
                    valor = candidata.perfilUrl
                )
                DatoCandidata(
                    icono = Icons.Default.DateRange,
                    titulo = "Fecha de registro",
                    valor = formatearFecha(candidata.fechaRegistro)
                )
                DatoCandidata(
                    icono = Icons.Default.Description,
                    titulo = "Currículum",
                    valor = candidata.cvNombre ?: "Sin CV adjunto"
                )
            }
        }

        Text(
            text = "Revisión de antecedentes",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Button(
            onClick = { abrirEnlace(context, candidata.perfilUrl) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Abrir perfil en el navegador")
        }

        OutlinedButton(
            onClick = {
                selectorCv.launch(
                    arrayOf(
                        "application/pdf",
                        "application/msword",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.AttachFile,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = if (candidata.cvNombre == null) "Adjuntar CV ficticio" else "Cambiar CV adjunto")
        }
    }
}

@Composable
fun DatoCandidata(
    icono: ImageVector,
    titulo: String,
    valor: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = valor,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

fun abrirEnlace(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "No hay una aplicación para abrir el enlace", Toast.LENGTH_SHORT).show()
    }
}

fun guardarPermisoLectura(context: Context, uri: Uri) {
    try {
        context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
    } catch (e: SecurityException) {
        Toast.makeText(context, "No se pudo guardar el permiso para leer el CV", Toast.LENGTH_SHORT).show()
    }
}

fun obtenerNombreArchivo(context: Context, uri: Uri): String {
    var nombre = "cv_adjunto"
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        val indiceNombre = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (indiceNombre >= 0 && it.moveToFirst()) {
            nombre = it.getString(indiceNombre)
        }
    }
    return nombre
}

fun formatearFecha(milisegundos: Long): String {
    val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return formato.format(Date(milisegundos))
}
