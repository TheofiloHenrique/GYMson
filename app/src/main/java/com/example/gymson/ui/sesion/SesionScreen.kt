package com.example.gymson.ui.sesion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.gymson.data.FakeData.rutinaDemo
import com.example.gymson.ui.theme.EntrenamientoPersonalTheme

@Composable
fun SesionScreen(
    onTerminar: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SesionViewModel = viewModel(),
    registroContent: @Composable () -> Unit = {} // hueco para la Etapa 5
) {
    val uiState by viewModel.uiState.collectAsState()
    SesionContent(
        uiState = uiState,
        onIniciarDescanso = viewModel::iniciarDescanso,
        onSaltarDescanso = viewModel::saltarDescanso,
        onAvanzar = { viewModel.avanzar(onTerminar) },
        onVolver = onTerminar,
        modifier = modifier,
        registroContent = registroContent
    )
}

@Composable
fun SesionContent(
    uiState: SesionUiState,
    onIniciarDescanso: () -> Unit,
    onSaltarDescanso: () -> Unit,
    onAvanzar: () -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    registroContent: @Composable () -> Unit = {}
) {
    val actual = uiState.ejercicioRutina
    if (actual == null) {
        Column(
            modifier = modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("No hay una rutina para entrenar.", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))
            Button(onClick = onVolver) { Text("Volver") }
        }
        return
    }

    val ejercicio = actual.ejercicio
    val unidad = if (ejercicio.category == "stretching") "s" else "reps"
    val textoBoton = when {
        !uiState.esUltimaSerie -> "Siguiente serie"
        !uiState.esUltimoEjercicio -> "Siguiente ejercicio"
        else -> "Terminar sesión"
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        // Zona con scroll: las instrucciones pueden ser largas
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Ejercicio ${uiState.indiceEjercicio + 1} de ${uiState.totalEjercicios}",
                style = MaterialTheme.typography.labelLarge
            )
            LinearProgressIndicator(
                progress = { (uiState.indiceEjercicio + 1f) / uiState.totalEjercicios },
                modifier = Modifier.fillMaxWidth()
            )
            Text(ejercicio.name, style = MaterialTheme.typography.headlineSmall)
            ejercicio.urlImagen()?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = ejercicio.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                )
            }
            Text(
                "Serie ${uiState.serieActual} de ${actual.series} · ${actual.repeticiones} $unidad",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            CuadroDescanso(
                uiState = uiState,
                onIniciar = onIniciarDescanso,
                onSaltar = onSaltarDescanso,
                descansoTotal = actual.descansoSegundos
            )

            registroContent() // TODO Etapa 5: RegistroSerieForm

            Text("Instrucciones", style = MaterialTheme.typography.titleMedium)
            ejercicio.instructions.forEachIndexed { i, paso ->
                Text("${i + 1}. $paso", style = MaterialTheme.typography.bodyMedium)
            }
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = onAvanzar, modifier = Modifier.fillMaxWidth()) { Text(textoBoton) }
    }
}

@Composable
private fun CuadroDescanso(
    uiState: SesionUiState,
    onIniciar: () -> Unit,
    onSaltar: () -> Unit,
    descansoTotal: Int,
    modifier: Modifier = Modifier
) {
    // El color cambia cuando termina el descanso (aviso visual, RF04)
    val colores = if (uiState.descansoTerminado) {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    } else {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    }
    Card(modifier = modifier.fillMaxWidth(), colors = colores) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when {
                uiState.descansando -> {
                    Text(
                        formatearTiempo(uiState.descansoRestante),
                        style = MaterialTheme.typography.displayMedium
                    )
                    OutlinedButton(onClick = onSaltar) { Text("Saltar descanso") }
                }
                uiState.descansoTerminado -> {
                    Text(
                        "¡Descanso terminado! Puedes iniciar la siguiente serie.",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    OutlinedButton(onClick = onIniciar) { Text("Descansar de nuevo") }
                }
                else -> {
                    Text("Descanso: ${formatearTiempo(descansoTotal)}")
                    Button(onClick = onIniciar) { Text("Iniciar descanso") }
                }
            }
        }
    }
}

private fun formatearTiempo(segundos: Int): String =
    "%02d:%02d".format(segundos / 60, segundos % 60)

@Preview(showBackground = true)
@Composable
private fun SesionPreview() {
    EntrenamientoPersonalTheme {
        SesionContent(
            uiState = SesionUiState(rutina = rutinaDemo),
            onIniciarDescanso = {}, onSaltarDescanso = {}, onAvanzar = {}, onVolver = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SesionDescansandoPreview() {
    EntrenamientoPersonalTheme {
        SesionContent(
            uiState = SesionUiState(rutina = rutinaDemo, descansando = true, descansoRestante = 45),
            onIniciarDescanso = {}, onSaltarDescanso = {}, onAvanzar = {}, onVolver = {}
        )
    }
}