package com.example.gymson.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.gymson.R
import com.example.gymson.ui.theme.EntrenamientoPersonalTheme
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.graphicsLayer
import com.example.gymson.data.FakeData.ejerciciosDemo
import com.example.gymson.ui.util.urlsImagenes
import kotlinx.coroutines.delay

// Imagen de ejercicio con esquinas redondeadas, borde fino y estados de carga/error.
// El tamaño lo define quien la usa (size, aspectRatio...). Coil hace el crossfade al cargar.
@Composable
fun ImagenEjercicio(
    url: String?,
    descripcion: String,
    modifier: Modifier = Modifier,
    forma: Shape = MaterialTheme.shapes.medium,
    contentScale: ContentScale = ContentScale.Crop
) {
    // remember(url): si cambia la URL (otro ejercicio) los estados se reinician
    var cargando by remember(url) { mutableStateOf(url != null) }
    var fallo by remember(url) { mutableStateOf(url == null) }
    val contexto = LocalContext.current
    val peticion = remember(url) {
        ImageRequest.Builder(contexto).data(url).crossfade(true).build()
    }

    Box(
        modifier = modifier
            .clip(forma)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, forma),
        contentAlignment = Alignment.Center
    ) {
        if (cargando) CajaEsqueleto(Modifier.fillMaxSize(), forma)
        if (!fallo) {
            AsyncImage(
                model = peticion,
                contentDescription = descripcion,
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize(),
                onSuccess = { cargando = false },
                onError = { cargando = false; fallo = true }
            )
        }
        if (fallo) {
            Icon(
                painter = painterResource(R.drawable.ic_fitness_center),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

// Alterna entre los 2 cuadros de la API con un fundido, para mostrar el movimiento.
// - Los dos AsyncImage están siempre compuestos (el segundo con alfa 0): así Coil
//   precarga el cuadro 2 y no hay parpadeo cuando aparece.
// - Si el ejercicio tiene 1 sola imagen, o el cuadro 2 falla, queda estática (igual que antes).
@Composable
fun ImagenEjercicioAnimada(
    urls: List<String>,
    descripcion: String,
    modifier: Modifier = Modifier,
    forma: Shape = MaterialTheme.shapes.medium,
    contentScale: ContentScale = ContentScale.Crop,
    intervaloMs: Long = 800L
) {
    val primera = urls.getOrNull(0)
    val segunda = urls.getOrNull(1)

    // Sin segundo cuadro no hay nada que animar: se reutiliza el componente estático
    if (segunda == null) {
        ImagenEjercicio(primera, descripcion, modifier, forma, contentScale)
        return
    }

    // Se usan los textos de las URLs como claves (no la lista, que es nueva en cada recomposición)
    var segundaLista by remember(segunda) { mutableStateOf(false) }
    var segundaFalla by remember(segunda) { mutableStateOf(false) }
    var verSegunda by remember(primera, segunda) { mutableStateOf(false) }

    // Empieza a alternar solo cuando el cuadro 2 ya cargó. Se cancela solo al salir de la pantalla.
    LaunchedEffect(segundaLista) {
        if (segundaLista) {
            while (true) {
                delay(intervaloMs)
                verSegunda = !verSegunda
            }
        }
    }
    val alfa by animateFloatAsState(
        targetValue = if (verSegunda) 1f else 0f,
        animationSpec = tween(durationMillis = 250),
        label = "cuadroEjercicio"
    )

    val contexto = LocalContext.current
    val peticion = remember(segunda) { ImageRequest.Builder(contexto).data(segunda).build() }
    val colorBorde = MaterialTheme.colorScheme.outlineVariant

    Box(modifier = modifier.clip(forma)) {
        // Cuadro 1: el componente estático (con esqueleto de carga e ícono de error)
        ImagenEjercicio(
            url = primera,
            descripcion = descripcion,
            modifier = Modifier.fillMaxSize(),
            forma = forma,
            contentScale = contentScale
        )
        // Cuadro 2 encima, con alfa animado
        if (!segundaFalla) {
            AsyncImage(
                model = peticion,
                contentDescription = null,   // la descripción ya la lleva el cuadro 1
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize().graphicsLayer { this.alpha = alfa },
                onSuccess = { segundaLista = true },
                onError = { segundaFalla = true }
            )
        }
        // El borde de 1 dp se dibuja al final para que el cuadro 2 no lo tape
        Box(Modifier.fillMaxSize().border(1.dp, colorBorde, forma))
    }
}

@Preview(showBackground = true)
@Composable
private fun ImagenEjercicioPreview() {
    EntrenamientoPersonalTheme {
        // Sin red en el Preview se ve el estado de carga; con url = null, el de error
        ImagenEjercicio(url = null, descripcion = "Pushups", modifier = Modifier.size(88.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun ImagenEjercicioAnimadaPreview() {
    EntrenamientoPersonalTheme {
        // Sin red en el Preview se ve el esqueleto; en el emulador se anima
        ImagenEjercicioAnimada(
            urls = ejerciciosDemo.first().urlsImagenes(),
            descripcion = "Pushups",
            modifier = Modifier.size(200.dp)
        )
    }
}
