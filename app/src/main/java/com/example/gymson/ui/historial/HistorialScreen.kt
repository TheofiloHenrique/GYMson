package com.example.gymson.ui.historial

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymson.R
import com.example.gymson.data.FakeData.sesionesDemo
import com.example.gymson.data.model.SesionCompletada
import com.example.gymson.ui.components.TarjetaGymson
import com.example.gymson.ui.components.aparecerEscalonado
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
    // Estado vacío: es un ESTADO de la pantalla (no una pestaña)
    if (uiState.sesiones.isEmpty()) {
        EstadoVacio(modifier)
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(key = "resumen") {
            TarjetaResumen(uiState.sesiones, Modifier.aparecerEscalonado(0))
        }
        item(key = "titulo") {
            Text(
                text = "Sesiones recientes",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
            )
        }
        // fechaMillis como clave: Compose reutiliza los elementos al agregar una sesión nueva
        itemsIndexed(uiState.sesiones, key = { _, sesion -> sesion.fechaMillis }) { indice, sesion ->
            TarjetaSesion(sesion, Modifier.aparecerEscalonado(indice + 1))
        }
    }
}

// ---------- Resumen (RF07) ----------

@Composable
private fun TarjetaResumen(sesiones: List<SesionCompletada>, modifier: Modifier = Modifier) {
    val total = sesiones.size
    val series = sesiones.sumOf { it.series.size }
    val minutos = sesiones.sumOf { it.duracionMinutos }

    TarjetaGymson(
        modifier = modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "TU PROGRESO",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            )
            Text(
                text = if (total == 1) "1 sesión completada" else "$total sesiones completadas",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Estadistica("$series", "Series totales", Modifier.weight(1f))
                Estadistica("$minutos", "Minutos totales", Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun Estadistica(valor: String, etiqueta: String, modifier: Modifier = Modifier) {
    TarjetaGymson(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = valor,
                // "tnum": cifras de ancho fijo
                style = MaterialTheme.typography.headlineSmall.copy(fontFeatureSettings = "tnum")
            )
            Text(
                text = etiqueta,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ---------- Lista de sesiones ----------

@Composable
private fun TarjetaSesion(sesion: SesionCompletada, modifier: Modifier = Modifier) {
    val volumen = sesion.volumenKg()
    TarjetaGymson(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = formatearFecha(sesion.fechaMillis),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(sesion.nombreRutina, style = MaterialTheme.typography.titleMedium)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DatoSesion(R.drawable.ic_check_circle, "${sesion.series.size} series")
                DatoSesion(R.drawable.ic_timer, "${sesion.duracionMinutos} min")
                // Con peso corporal la carga es 0 y el volumen no dice nada
                DatoSesion(
                    R.drawable.ic_fitness_center,
                    if (volumen == 0f) "Peso corporal" else "${volumen.roundToInt()} kg"
                )
            }
        }
    }
}

// Chip pequeño con ícono + texto
@Composable
private fun DatoSesion(@DrawableRes icono: Int, texto: String) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            painter = painterResource(icono),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = texto,
            style = MaterialTheme.typography.labelMedium.copy(fontFeatureSettings = "tnum"),
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

// ---------- Estado vacío ----------

@Composable
private fun EstadoVacio(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        TarjetaGymson(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_history),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Text(
                    "Aún no hay sesiones",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
                Text(
                    "Cuando termines tu primer entrenamiento aparecerá aquí, con su duración, series y volumen.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
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

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HistorialOscuroPreview() {
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