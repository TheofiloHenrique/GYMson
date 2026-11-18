package com.example.gymson.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

// Reduce levemente el elemento mientras se presiona (retroalimentación táctil).
// La escala se lee dentro de graphicsLayer: así solo se redibuja, no se recompone.
@Composable
fun Modifier.escalaAlPresionar(
    fuente: InteractionSource,
    escalaPresionada: Float = 0.97f
): Modifier {
    val presionado by fuente.collectIsPressedAsState()
    val escala by animateFloatAsState(
        targetValue = if (presionado) escalaPresionada else 1f,
        label = "escalaAlPresionar"
    )
    return this.graphicsLayer {
        scaleX = escala
        scaleY = escala
    }
}

// Entrada escalonada: el elemento aparece con fundido y un pequeño deslizamiento hacia arriba,
// con un retraso proporcional a su posición (los primeros 6; el resto sin retraso).
// - graphicsLayer (y no AnimatedVisibility): el espacio del elemento ya está reservado,
//   así la lista no "salta" mientras los elementos van apareciendo.
// - rememberSaveable: la animación se ve una sola vez; al cambiar de pestaña y volver no se repite.
@Composable
fun Modifier.aparecerEscalonado(
    indice: Int,
    retrasoPorElementoMs: Long = 60L
): Modifier {
    var yaMostrado by rememberSaveable { mutableStateOf(false) }
    val progreso = remember { Animatable(if (yaMostrado) 1f else 0f) }
    LaunchedEffect(Unit) {
        if (!yaMostrado) {
            if (indice < 6) delay(indice * retrasoPorElementoMs)
            progreso.animateTo(1f, tween(durationMillis = 350))
            yaMostrado = true
        }
    }
    return this.graphicsLayer {
        alpha = progreso.value
        translationY = (1f - progreso.value) * 24.dp.toPx()
    }
}
