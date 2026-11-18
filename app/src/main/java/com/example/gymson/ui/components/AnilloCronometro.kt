package com.example.gymson.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymson.ui.theme.EntrenamientoPersonalTheme

// Anillo coral que se vacía con la cuenta regresiva del descanso.
// animateFloatAsState con tween(1000, LinearEasing): como el valor cambia cada segundo,
// el arco se desliza de forma continua en lugar de dar saltos.
@Composable
fun AnilloCronometro(
    restanteSegundos: Int,
    totalSegundos: Int,
    modifier: Modifier = Modifier,
    etiqueta: String? = null
) {
    val fraccion = if (totalSegundos > 0) {
        restanteSegundos.coerceIn(0, totalSegundos) / totalSegundos.toFloat()
    } else 0f
    val animada by animateFloatAsState(
        targetValue = fraccion,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        label = "anilloCronometro"
    )
    val color = MaterialTheme.colorScheme.tertiary
    val tiempo = formatearTiempo(restanteSegundos)

    Box(
        modifier = modifier
            .size(200.dp)
            .semantics(mergeDescendants = true) { contentDescription = "Descanso: $tiempo" },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val grosor = 12.dp.toPx()
            val esquina = Offset(grosor / 2, grosor / 2)
            val area = Size(size.width - grosor, size.height - grosor)
            // Pista (círculo completo, tenue)
            drawArc(
                color = color.copy(alpha = 0.25f),
                startAngle = 0f, sweepAngle = 360f, useCenter = false,
                topLeft = esquina, size = area, style = Stroke(width = grosor)
            )
            // Progreso: empieza arriba (-90°) y se acorta con el tiempo
            if (animada > 0f) {
                drawArc(
                    color = color,
                    startAngle = -90f, sweepAngle = 360f * animada, useCenter = false,
                    topLeft = esquina, size = area,
                    style = Stroke(width = grosor, cap = StrokeCap.Round)
                )
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = tiempo,
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (etiqueta != null) {
                Text(
                    text = etiqueta,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun formatearTiempo(segundos: Int): String =
    "%02d:%02d".format(segundos / 60, segundos % 60)

@Preview(showBackground = true)
@Composable
private fun AnilloCronometroPreview() {
    EntrenamientoPersonalTheme {
        AnilloCronometro(restanteSegundos = 68, totalSegundos = 90, etiqueta = "SIGUIENTE: SERIE 2")
    }
}
