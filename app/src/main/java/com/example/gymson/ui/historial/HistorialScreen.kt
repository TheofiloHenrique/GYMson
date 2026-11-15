package com.example.gymson.ui.historial

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymson.data.FakeData.sesionesDemo
import com.example.gymson.data.model.SesionCompletada
import com.example.gymson.ui.theme.EntrenamientoPersonalTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

private val formatoFecha: DateTimeFormatter =
    DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm", Locale.forLanguageTag("es"))

// Versión con ViewModel (la que usa el NavGraph)
@Composable
fun HistorialScreen(
    modifier: Modifier = Modifier,
    viewModel: HistorialViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    HistorialContent(uiState = uiState, modifier = modifier)
}

// Versión sin ViewModel: solo recibe estado -> fácil de previsualizar
@Composable
fun HistorialContent(
    uiState: HistorialUiState,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Historial", style = MaterialTheme.typography.headlineMedium)

        // Resumen superior (RF07)
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Text(
                text = "Sesiones completadas: ${uiState.totalSesiones}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )
        }

        if (uiState.sesiones.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    "Aún no has completado ninguna sesión. ¡Tu primer entrenamiento aparecerá aquí!",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.sesiones) { sesion -> TarjetaSesion(sesion) }
            }
        }
    }
}

@Composable
private fun TarjetaSesion(sesion: SesionCompletada, modifier: Modifier = Modifier) {
    val volumen = sesion.volumenKg()
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(formatearFecha(sesion.fechaMillis), style = MaterialTheme.typography.labelMedium)
            Text(sesion.nombreRutina, style = MaterialTheme.typography.titleMedium)
            Text(
                "${sesion.series.size} series · ${sesion.duracionMinutos} min",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                // Con peso corporal la carga es 0 y el volumen no dice nada
                if (volumen == 0f) "Sin carga externa (peso corporal)"
                else "Volumen total: ${volumen.roundToInt()} kg",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun formatearFecha(millis: Long): String =
    Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).format(formatoFecha)

@Preview(showBackground = true)
@Composable
private fun HistorialPreview() {
    EntrenamientoPersonalTheme {
        HistorialContent(HistorialUiState(sesionesDemo))
    }
}

@Preview(showBackground = true)
@Composable
private fun HistorialVacioPreview() {
    EntrenamientoPersonalTheme {
        HistorialContent(HistorialUiState())
    }
}