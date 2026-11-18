package com.example.gymson.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

// Botón de avance "Siguiente ejercicio": azul marino (secundario de marca).
// En modo oscuro el tema lo vuelve azul claro con texto oscuro (contraste).
@Composable
fun BotonAzulMarino(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    @DrawableRes icono: Int? = null
) {
    val fuente = remember { MutableInteractionSource() }
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 56.dp).escalaAlPresionar(fuente),
        enabled = enabled,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        ),
        interactionSource = fuente
    ) {
        Text(texto, style = MaterialTheme.typography.labelLarge)
        if (icono != null) {
            Spacer(Modifier.width(8.dp))
            Icon(painterResource(icono), contentDescription = null, modifier = Modifier.size(20.dp))
        }
    }
}