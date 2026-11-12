package com.example.gymson.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymson.data.model.Objetivo
import com.example.gymson.ui.theme.EntrenamientoPersonalTheme

//Selector reutilizable: muestra un título y una fila de chips; solo uno queda seleccionado.
//<T> permite usarlo con enums (Objetivo, Nivel...) y con números (días por semana).
@Composable
fun <T> SelectorOpciones(
    titulo: String,
    opciones: List<T>,
    seleccionada: T?,
    onSeleccion: (T) -> Unit,
    modifier: Modifier = Modifier,
    etiqueta: (T) -> String = { it.toString() }
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = titulo, style = MaterialTheme.typography.titleMedium)
        //FlowRow pasa los chips a la siguiente línea si no caben (5 equipamientos)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            opciones.forEach { opcion ->
                FilterChip(
                    selected = opcion == seleccionada,
                    onClick = { onSeleccion(opcion) },
                    label = { Text(etiqueta(opcion)) }
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
            onSeleccion = {}
        )
    }
}