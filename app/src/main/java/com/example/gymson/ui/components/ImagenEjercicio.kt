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

@Preview(showBackground = true)
@Composable
private fun ImagenEjercicioPreview() {
    EntrenamientoPersonalTheme {
        // Sin red en el Preview se ve el estado de carga; con url = null, el de error
        ImagenEjercicio(url = null, descripcion = "Pushups", modifier = Modifier.size(88.dp))
    }
}
