package com.example.gymson.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymson.ui.theme.EntrenamientoPersonalTheme

// Caja gris que "respira" (la opacidad sube y baja) mientras se carga el contenido.
// Una sola InfiniteTransition por caja; el alfa se lee en graphicsLayer (solo redibuja).
@Composable
fun CajaEsqueleto(
    modifier: Modifier = Modifier,
    forma: Shape = MaterialTheme.shapes.small
) {
    val transicion = rememberInfiniteTransition(label = "esqueleto")
    val alfa by transicion.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alfaEsqueleto"
    )
    Box(
        modifier = modifier
            .clip(forma)
            .graphicsLayer { this.alpha = alfa }
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
    )
}

// Silueta de una tarjeta de ejercicio (miniatura + tres líneas de texto)
@Composable
fun TarjetaEsqueleto(modifier: Modifier = Modifier) {
    TarjetaGymson(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CajaEsqueleto(Modifier.size(72.dp), MaterialTheme.shapes.medium)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CajaEsqueleto(Modifier.fillMaxWidth(0.7f).height(16.dp))
                CajaEsqueleto(Modifier.fillMaxWidth(0.4f).height(12.dp))
                CajaEsqueleto(Modifier.fillMaxWidth(0.55f).height(12.dp))
            }
        }
    }
}

@Composable
fun ListaEsqueleto(cantidad: Int = 5, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(cantidad) { TarjetaEsqueleto() }
    }
}

@Preview(showBackground = true)
@Composable
private fun ListaEsqueletoPreview() {
    EntrenamientoPersonalTheme {
        ListaEsqueleto(cantidad = 3, modifier = Modifier.padding(16.dp))
    }
}
