package com.example.gymson.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.gymson.data.model.Ejercicio
import com.example.gymson.data.FakeData.ejerciciosDemo
import com.example.gymson.ui.theme.EntrenamientoPersonalTheme

@Composable
fun TarjetaEjercicio(
    ejercicio: Ejercicio,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp)) {
            // Coil descarga y cachea la imagen automáticamente
            AsyncImage(
                model = ejercicio.urlImagen(),
                contentDescription = ejercicio.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(88.dp)
            )
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(text = ejercicio.name, style = MaterialTheme.typography.titleMedium)
                Text(text = "Nivel: ${ejercicio.level}", style = MaterialTheme.typography.bodyMedium)
                // equipment puede ser null en el JSON
                Text(
                    text = "Equipo: ${ejercicio.equipment ?: "ninguno"}",
                    style = MaterialTheme.typography.bodyMedium
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