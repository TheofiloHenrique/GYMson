package com.example.gymson.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymson.data.FakeData.ejerciciosDemo
import com.example.gymson.data.model.Ejercicio
import com.example.gymson.ui.theme.EntrenamientoPersonalTheme

// Los textos (nombre, nivel, equipo) siguen en inglés hasta la fase de traducción (7.10).
@Composable
fun TarjetaEjercicio(
    ejercicio: Ejercicio,
    modifier: Modifier = Modifier
) {
    TarjetaGymson(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ImagenEjercicio(
                url = ejercicio.urlImagen(),
                descripcion = ejercicio.name,
                modifier = Modifier.size(80.dp)
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = ejercicio.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Nivel: ${ejercicio.level}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                // equipment puede ser null en el JSON
                Text(
                    text = "Equipo: ${ejercicio.equipment ?: "ninguno"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TarjetaEjercicioPreview() {
    EntrenamientoPersonalTheme {
        TarjetaEjercicio(ejercicio = ejerciciosDemo.first())
    }
}
