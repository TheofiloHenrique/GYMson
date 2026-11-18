package com.example.gymson.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymson.R
import com.example.gymson.data.model.Objetivo
import com.example.gymson.ui.theme.EntrenamientoPersonalTheme

// Selector reutilizable: una fila de chips; solo uno queda seleccionado.
// <T> permite usarlo con enums (Objetivo, Nivel...) y con números (días por semana).
// - titulo: opcional (null = sin título; la pantalla de Perfil lo dibuja en su tarjeta de sección).
// - icono: opcional; devuelve el recurso del ícono de cada opción (o null).
@Composable
fun <T> SelectorOpciones(
    titulo: String? = null,
    opciones: List<T>,
    seleccionada: T?,
    onSeleccion: (T) -> Unit,
    modifier: Modifier = Modifier,
    etiqueta: (T) -> String = { it.toString() },
    icono: (T) -> Int? = { null }
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (titulo != null) Text(text = titulo, style = MaterialTheme.typography.titleMedium)
        // FlowRow pasa los chips a la siguiente línea si no caben (5 equipamientos)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            opciones.forEach { opcion ->
                val activa = opcion == seleccionada
                @DrawableRes val iconoRes = icono(opcion)
                FilterChip(
                    selected = activa,
                    onClick = { onSeleccion(opcion) },
                    label = { Text(etiqueta(opcion), style = MaterialTheme.typography.labelLarge) },
                    leadingIcon = if (iconoRes != null) {
                        {
                            Icon(
                                painter = painterResource(iconoRes),
                                contentDescription = null,
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else null,
                    shape = CircleShape,
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        labelColor = MaterialTheme.colorScheme.secondary,
                        iconColor = MaterialTheme.colorScheme.secondary,
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = activa,
                        borderColor = MaterialTheme.colorScheme.outlineVariant,
                        selectedBorderColor = Color.Transparent
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectorOpcionesPreview() {
    EntrenamientoPersonalTheme {
        SelectorOpciones(
            titulo = "Objetivo",
            opciones = Objetivo.entries,
            seleccionada = Objetivo.FUERZA,
            onSeleccion = {},
            icono = { R.drawable.ic_bolt }
        )
    }
}
