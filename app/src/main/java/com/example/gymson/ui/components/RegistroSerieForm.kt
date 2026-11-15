package com.example.gymson.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymson.ui.theme.EntrenamientoPersonalTheme

// Formulario reutilizable (RF05): repeticiones + carga en kg + botón «Registrar serie».
// No sabe nada de la sesión: solo avisa con onRegistrar(repeticiones, cargaKg).
// El texto de los campos es estado local de la UI (no es lógica de negocio), por eso vive aquí.
@Composable
fun RegistroSerieForm(
    repsIniciales: Int,
    cargaInicial: Float?,
    registrada: Boolean,
    onRegistrar: (repeticiones: Int, cargaKg: Float) -> Unit,
    modifier: Modifier = Modifier,
    etiquetaReps: String = "Repeticiones"
) {
    // rememberSaveable: el texto sobrevive a una rotación de pantalla
    var reps by rememberSaveable { mutableStateOf(repsIniciales.toString()) }
    var carga by rememberSaveable { mutableStateOf(cargaInicial?.let { formatearCarga(it) } ?: "") }

    val repsValidas = (reps.toIntOrNull() ?: 0) > 0
    // Carga vacía = 0 kg (ejercicios con peso corporal); acepta coma o punto decimal
    val cargaKg: Float? = if (carga.isBlank()) 0f else carga.replace(',', '.').toFloatOrNull()
    val puedeRegistrar = repsValidas && cargaKg != null

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Registrar serie", style = MaterialTheme.typography.titleMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = reps,
                    onValueChange = { reps = it.filter(Char::isDigit) },
                    label = { Text(etiquetaReps) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = carga,
                    onValueChange = { nuevo -> carga = nuevo.filter { it.isDigit() || it == '.' || it == ',' } },
                    label = { Text("Carga (kg)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
            }

            if (registrada) {
                Text(
                    "✓ Serie registrada",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Button(
                onClick = {
                    val r = reps.toIntOrNull()
                    if (r != null && cargaKg != null) onRegistrar(r, cargaKg)
                },
                enabled = puedeRegistrar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (registrada) "Actualizar registro" else "Registrar serie")
            }
        }
    }
}

// 20.0 -> "20"; 22.5 -> "22.5"
private fun formatearCarga(valor: Float): String =
    if (valor % 1f == 0f) valor.toInt().toString() else valor.toString()

@Preview(showBackground = true)
@Composable
private fun RegistroSerieFormPreview() {
    EntrenamientoPersonalTheme {
        RegistroSerieForm(
            repsIniciales = 6,
            cargaInicial = 20f,
            registrada = false,
            onRegistrar = { _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RegistroSerieFormRegistradaPreview() {
    EntrenamientoPersonalTheme {
        RegistroSerieForm(
            repsIniciales = 6,
            cargaInicial = null,
            registrada = true,
            onRegistrar = { _, _ -> }
        )
    }
}