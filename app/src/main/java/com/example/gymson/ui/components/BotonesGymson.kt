package com.example.gymson.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymson.R
import com.example.gymson.ui.theme.EntrenamientoPersonalTheme

// Acción principal (coral, texto oscuro). Solo para "Comenzar entrenamiento", "Comenzar serie"...
@Composable
fun BotonPrincipal(
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
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.onTertiary
        ),
        interactionSource = fuente
    ) { ContenidoBoton(texto, icono) }
}

// Acción secundaria: color primario del tema (verde en claro, menta en oscuro)
@Composable
fun BotonSecundario(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    @DrawableRes icono: Int? = null
) {
    val fuente = remember { MutableInteractionSource() }
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 52.dp).escalaAlPresionar(fuente),
        enabled = enabled,
        shape = MaterialTheme.shapes.medium,
        interactionSource = fuente
    ) { ContenidoBoton(texto, icono) }
}

// Acción terciaria: fondo de tarjeta, borde de 1 dp y texto azul marino
@Composable
fun BotonTerciario(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    @DrawableRes icono: Int? = null
) {
    val fuente = remember { MutableInteractionSource() }
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = 52.dp).escalaAlPresionar(fuente),
        enabled = enabled,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            contentColor = MaterialTheme.colorScheme.secondary
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        interactionSource = fuente
    ) { ContenidoBoton(texto, icono) }
}

@Composable
private fun RowScope.ContenidoBoton(texto: String, @DrawableRes icono: Int?) {
    if (icono != null) {
        Icon(painterResource(icono), contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
    }
    Text(texto, style = MaterialTheme.typography.labelLarge)
}

@Preview(showBackground = true)
@Composable
private fun BotonesPreview() {
    EntrenamientoPersonalTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            BotonPrincipal("Comenzar entrenamiento", {}, Modifier.fillMaxWidth(), icono = R.drawable.ic_play_arrow)
            BotonSecundario("Guardar", {}, Modifier.fillMaxWidth().padding(top = 8.dp), icono = R.drawable.ic_check)
            BotonTerciario("Saltar descanso", {}, Modifier.fillMaxWidth().padding(top = 8.dp))
        }
    }
}
