package com.example.gymson.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymson.R
import com.example.gymson.ui.theme.EntrenamientoPersonalTheme

// Logo compacto + nombre de la pantalla. windowInsetsPadding evita quedar bajo la barra de estado.
@Composable
fun EncabezadoGymson(titulo: String, modifier: Modifier = Modifier) {
    val oscuro = isSystemInDarkTheme()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Image(
            painter = painterResource(
                if (oscuro) R.drawable.logo_compacta_oscuro else R.drawable.logo_compacta
            ),
            contentDescription = "GYMson",
            modifier = Modifier.height(32.dp)
        )
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EncabezadoPreview() {
    EntrenamientoPersonalTheme { EncabezadoGymson("Rutina del día") }
}