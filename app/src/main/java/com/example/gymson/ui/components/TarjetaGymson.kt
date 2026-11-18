package com.example.gymson.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymson.ui.theme.EntrenamientoPersonalTheme

// Tarjeta plana de la app: sin sombra, borde de 1 dp y esquinas de 20 dp (tema).
// Si se pasa onClick, además reacciona al toque con una pequeña animación de escala.
@Composable
fun TarjetaGymson(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val forma = MaterialTheme.shapes.large
    val colores = CardDefaults.cardColors(containerColor = containerColor)
    val elevacion = CardDefaults.cardElevation(defaultElevation = 0.dp)
    val borde = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)

    if (onClick == null) {
        Card(
            modifier = modifier,
            shape = forma,
            colors = colores,
            elevation = elevacion,
            border = borde,
            content = content
        )
    } else {
        val fuente = remember { MutableInteractionSource() }
        Card(
            onClick = onClick,
            modifier = modifier.escalaAlPresionar(fuente),
            shape = forma,
            colors = colores,
            elevation = elevacion,
            border = borde,
            interactionSource = fuente,
            content = content
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TarjetaGymsonPreview() {
    EntrenamientoPersonalTheme {
        TarjetaGymson(onClick = {}) {
            Text("Tren superior", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
        }
    }
}
