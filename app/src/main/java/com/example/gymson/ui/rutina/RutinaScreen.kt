package com.example.gymson.ui.rutina

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.gymson.data.FakeData.rutinaDemo
import com.example.gymson.data.model.EjercicioRutina
import com.example.gymson.data.model.Rutina
import com.example.gymson.ui.components.EstadoCargando
import com.example.gymson.ui.components.EstadoError
import com.example.gymson.ui.theme.EntrenamientoPersonalTheme

// Versión con ViewModel (la que usa el NavGraph)
@Composable
fun RutinaScreen(
    onComenzar: () -> Unit,
    onIrAPerfil: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RutinaViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    RutinaContent(
        uiState = uiState,
        onComenzar = onComenzar,
        onIrAPerfil = onIrAPerfil,
        onReintentar = viewModel::cargar,
        modifier = modifier
    )
}

// Versión sin ViewModel: solo estado y lambdas -> fácil de previsualizar
@Composable
fun RutinaContent(
    uiState: RutinaUiState,
    onComenzar: () -> Unit,
    onIrAPerfil: () -> Unit,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        is RutinaUiState.Cargando -> EstadoCargando(modifier)
        is RutinaUiState.Error -> EstadoError(uiState.mensaje, onReintentar, modifier)
        is RutinaUiState.SinPerfil -> MensajeConBoton(
            mensaje = "Aún no tienes un perfil. Cuéntanos tu objetivo y tu equipamiento para armar tu rutina.",
            textoBoton = "Crear perfil",
            onClick = onIrAPerfil,
            modifier = modifier
        )
        is RutinaUiState.Exito ->
            if (uiState.rutina.ejercicios.isEmpty()) {
                MensajeConBoton(
                    mensaje = "No encontramos ejercicios que coincidan con tu perfil. Prueba cambiando el nivel o el equipamiento.",
                    textoBoton = "Editar perfil",
                    onClick = onIrAPerfil,
                    modifier = modifier
                )
            } else {
                RutinaLista(uiState.rutina, onComenzar, modifier)
            }
    }
}

@Composable
private fun RutinaLista(
    rutina: Rutina,
    onComenzar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Rutina de hoy", style = MaterialTheme.typography.bodyMedium)
        Text(text = rutina.nombre, style = MaterialTheme.typography.headlineMedium)
        Text(
            text = "${rutina.ejercicios.size} ejercicios",
            style = MaterialTheme.typography.bodyMedium
        )

        // weight(1f): la lista ocupa el espacio sobrante y el botón queda siempre visible abajo
        LazyColumn(
            modifier = Modifier.weight(1f).padding(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(rutina.ejercicios, key = { it.ejercicio.id }) { item ->
                TarjetaEjercicioRutina(item)
            }
        }

        Button(onClick = onComenzar, modifier = Modifier.fillMaxWidth()) {
            Text("Comenzar entrenamiento")
        }
    }
}

// Propia de esta pantalla: muestra series × repeticiones, que TarjetaEjercicio (Etapa 1) no tiene
@Composable
private fun TarjetaEjercicioRutina(
    item: EjercicioRutina,
    modifier: Modifier = Modifier
) {
    // En los estiramientos "repeticiones" se interpreta como segundos
    val unidad = if (item.ejercicio.category == "stretching") "s" else "reps"

    Card(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = item.ejercicio.urlImagen(),
                contentDescription = item.ejercicio.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(72.dp)
            )
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(text = item.ejercicio.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${item.series} series × ${item.repeticiones} $unidad",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Descanso: ${item.descansoSegundos} s",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun MensajeConBoton(
    mensaje: String,
    textoBoton: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = mensaje,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
        Button(onClick = onClick, modifier = Modifier.padding(top = 16.dp)) {
            Text(textoBoton)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RutinaExitoPreview() {
    EntrenamientoPersonalTheme {
        RutinaContent(
            uiState = RutinaUiState.Exito(rutinaDemo),
            onComenzar = {}, onIrAPerfil = {}, onReintentar = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RutinaSinPerfilPreview() {
    EntrenamientoPersonalTheme {
        RutinaContent(
            uiState = RutinaUiState.SinPerfil,
            onComenzar = {}, onIrAPerfil = {}, onReintentar = {}
        )
    }
}