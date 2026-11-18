package com.example.gymson.ui.rutina

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymson.R
import com.example.gymson.data.FakeData.rutinaDemo
import com.example.gymson.data.model.EjercicioRutina
import com.example.gymson.ui.components.BotonPrincipal
import com.example.gymson.ui.components.BotonSecundario
import com.example.gymson.ui.components.EstadoCargando
import com.example.gymson.ui.components.EstadoError
import com.example.gymson.ui.components.ImagenEjercicio
import com.example.gymson.ui.components.TarjetaGymson
import com.example.gymson.ui.components.aparecerEscalonado
import com.example.gymson.ui.theme.EntrenamientoPersonalTheme
import com.example.gymson.ui.util.musculoEs

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
                RutinaLista(uiState, onComenzar, modifier)
            }
    }
}

@Composable
private fun RutinaLista(
    estado: RutinaUiState.Exito,
    onComenzar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        // weight(1f): la lista ocupa el espacio sobrante y el botón queda siempre visible abajo
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item(key = "hoy_toca") {
                TarjetaHoyToca(estado, Modifier.aparecerEscalonado(0))
            }
            item(key = "titulo_secuencia") {
                Text(
                    text = "Secuencia del día",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                )
            }
            itemsIndexed(estado.rutina.ejercicios, key = { _, item -> item.ejercicio.id }) { indice, item ->
                TarjetaEjercicioRutina(
                    item = item,
                    nombre = estado.traducciones[item.ejercicio.name] ?: item.ejercicio.name,
                    posicion = indice + 1,
                    modifier = Modifier.aparecerEscalonado(indice + 1)
                )
            }
        }
        BotonPrincipal(
            texto = "Comenzar entrenamiento",
            onClick = onComenzar,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            icono = R.drawable.ic_play_arrow
        )
    }
}

// Tarjeta menta "HOY TOCA": nombre de la sesión, resumen y ciclo semanal
@Composable
private fun TarjetaHoyToca(estado: RutinaUiState.Exito, modifier: Modifier = Modifier) {
    val cantidad = estado.rutina.ejercicios.size
    val dias = estado.diasPorSemana.coerceAtLeast(1)
    val indiceHoy = estado.numeroSesion % dias

    TarjetaGymson(
        modifier = modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "HOY TOCA",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            )
            Text(
                text = estado.rutina.nombre,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            // ≈ 8 min por ejercicio: la misma regla que usa generarRutina
            Text(
                text = "Sesión ${estado.numeroSesion + 1} · ≈ ${cantidad * 8} min · $cantidad ejercicios",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            CicloSemanal(dias = dias, indiceHoy = indiceHoy)
        }
    }
}

private enum class EstadoDia { HECHO, HOY, PENDIENTE }

@Composable
private fun CicloSemanal(dias: Int, indiceHoy: Int, modifier: Modifier = Modifier) {
    TarjetaGymson(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Ciclo semanal", style = MaterialTheme.typography.labelLarge)
                Text(
                    "Día ${indiceHoy + 1} de $dias",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(dias) { i ->
                    val estado = when {
                        i < indiceHoy -> EstadoDia.HECHO
                        i == indiceHoy -> EstadoDia.HOY
                        else -> EstadoDia.PENDIENTE
                    }
                    DiaCiclo(numero = i + 1, estado = estado, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun DiaCiclo(numero: Int, estado: EstadoDia, modifier: Modifier = Modifier) {
    val colores = MaterialTheme.colorScheme
    val (fondo, contenido) = when (estado) {
        EstadoDia.HECHO -> colores.surfaceContainer to colores.primary
        EstadoDia.HOY -> colores.primary to colores.onPrimary
        EstadoDia.PENDIENTE -> Color.Transparent to colores.onSurfaceVariant
    }
    @DrawableRes val icono = when (estado) {
        EstadoDia.HECHO -> R.drawable.ic_check_circle
        EstadoDia.HOY -> R.drawable.ic_play_circle
        EstadoDia.PENDIENTE -> R.drawable.ic_circle_outline
    }
    val descripcion = when (estado) {
        EstadoDia.HECHO -> "Día $numero, hecho"
        EstadoDia.HOY -> "Día $numero, hoy"
        EstadoDia.PENDIENTE -> "Día $numero, pendiente"
    }
    val forma = MaterialTheme.shapes.small
    Column(
        modifier = modifier
            .semantics(mergeDescendants = true) { contentDescription = descripcion }
            .background(fondo, forma)
            .then(
                if (estado == EstadoDia.PENDIENTE) Modifier.border(1.dp, colores.outlineVariant, forma)
                else Modifier
            )
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            painter = painterResource(icono),
            contentDescription = null,
            tint = contenido,
            modifier = Modifier.size(20.dp)
        )
        Text("Día $numero", style = MaterialTheme.typography.labelMedium, color = contenido)
    }
}

// Propia de esta pantalla: muestra series × repeticiones y el número de orden sobre la imagen
@Composable
private fun TarjetaEjercicioRutina(
    item: EjercicioRutina,
    nombre: String,
    posicion: Int,
    modifier: Modifier = Modifier
) {
    // En los estiramientos "repeticiones" se interpreta como segundos
    val unidad = if (item.ejercicio.category == "stretching") "s" else "reps"
    val musculo = item.ejercicio.primaryMuscles.firstOrNull()?.let(::musculoEs)

    TarjetaGymson(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                ImagenEjercicio(
                    url = item.ejercicio.urlImagen(),
                    descripcion = item.ejercicio.name,
                    modifier = Modifier.size(72.dp)
                )
                Text(
                    text = "$posicion",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(4.dp)
                        .background(Color.Black.copy(alpha = 0.55f), CircleShape)
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (musculo != null) EtiquetaMusculo(musculo)
                Text(
                    text = nombre,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${item.series} × ${item.repeticiones} $unidad · Descanso ${item.descansoSegundos} s",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EtiquetaMusculo(texto: String) {
    Text(
        text = texto,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
            .padding(horizontal = 10.dp, vertical = 2.dp)
    )
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
        BotonSecundario(
            texto = textoBoton,
            onClick = onClick,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RutinaExitoPreview() {
    EntrenamientoPersonalTheme {
        RutinaContent(
            uiState = RutinaUiState.Exito(rutinaDemo, numeroSesion = 4, diasPorSemana = 3),
            onComenzar = {}, onIrAPerfil = {}, onReintentar = {}
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun RutinaExitoOscuroPreview() {
    EntrenamientoPersonalTheme {
        RutinaContent(
            uiState = RutinaUiState.Exito(rutinaDemo, numeroSesion = 4, diasPorSemana = 3),
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
