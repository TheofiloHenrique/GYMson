package com.example.gymson.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymson.R
import com.example.gymson.ui.theme.EntrenamientoPersonalTheme

// Formulario reutilizable (RF05): repeticiones + carga en kg con botones − / + y botón «Registrar serie».
// No sabe nada de la sesión: solo avisa con onRegistrar(repeticiones, cargaKg).
// El texto de los campos es estado local de la UI (no es lógica de negocio), por eso vive aquí.
// Los parámetros numeroSerie y objetivoReps son opcionales (solo cambian textos).
@Composable
fun RegistroSerieForm(
    repsIniciales: Int,
    cargaInicial: Float?,
    registrada: Boolean,
    onRegistrar: (repeticiones: Int, cargaKg: Float) -> Unit,
    modifier: Modifier = Modifier,
    etiquetaReps: String = "Repeticiones",
    numeroSerie: Int? = null,
    objetivoReps: String? = null
) {
    // rememberSaveable: el texto sobrevive a una rotación de pantalla
    var reps by rememberSaveable { mutableStateOf(repsIniciales.toString()) }
    var carga by rememberSaveable { mutableStateOf(cargaInicial?.let { formatearCarga(it) } ?: "") }

    val repsActuales = reps.toIntOrNull() ?: 0
    // Carga vacía = 0 kg (ejercicios con peso corporal); acepta coma o punto decimal
    val cargaKg: Float? = if (carga.isBlank()) 0f else carga.replace(',', '.').toFloatOrNull()
    val puedeRegistrar = repsActuales > 0 && cargaKg != null

    TarjetaGymson(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "REGISTRO ACTUAL",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
                // Aparece con fundido cuando la serie queda registrada
                AnimatedVisibility(visible = registrada) {
                    Text(
                        "✓ Serie registrada",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            CampoContador(
                etiqueta = etiquetaReps,
                valor = reps,
                onValor = { reps = it.filter(Char::isDigit) },
                onMenos = { reps = (repsActuales - 1).coerceAtLeast(1).toString() },
                onMas = { reps = (repsActuales + 1).toString() },
                pie = objetivoReps,
                decimal = false
            )

            CampoContador(
                etiqueta = "Carga (kg)",
                valor = carga,
                onValor = { nuevo -> carga = nuevo.filter { it.isDigit() || it == '.' || it == ',' } },
                onMenos = { carga = formatearCarga(((cargaKg ?: 0f) - PASO_CARGA).coerceAtLeast(0f)) },
                onMas = { carga = formatearCarga((cargaKg ?: 0f) + PASO_CARGA) },
                pie = "Deja 0 si es peso corporal",
                decimal = true
            )

            BotonSecundario(
                texto = if (registrada) "Actualizar registro"
                else "Registrar serie" + (numeroSerie?.let { " $it" } ?: ""),
                onClick = {
                    if (puedeRegistrar) onRegistrar(repsActuales, cargaKg ?: 0f)
                },
                enabled = puedeRegistrar,
                modifier = Modifier.fillMaxWidth(),
                icono = R.drawable.ic_check
            )
        }
    }
}

private const val PASO_CARGA = 2.5f

// Campo numérico con botones − / + de 56 dp. También se puede escribir el valor directamente.
@Composable
private fun CampoContador(
    etiqueta: String,
    valor: String,
    onValor: (String) -> Unit,
    onMenos: () -> Unit,
    onMas: () -> Unit,
    pie: String?,
    decimal: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            etiqueta,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer, MaterialTheme.shapes.medium)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BotonRedondo(R.drawable.ic_remove, "Disminuir ${etiqueta.lowercase()}", onMenos)
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BasicTextField(
                    value = valor,
                    onValueChange = onValor,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.headlineSmall.copy(
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFeatureSettings = "tnum"
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = if (decimal) KeyboardType.Decimal else KeyboardType.Number
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                if (pie != null) {
                    Text(
                        pie,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
            BotonRedondo(R.drawable.ic_add, "Aumentar ${etiqueta.lowercase()}", onMas)
        }
    }
}

@Composable
private fun BotonRedondo(
    @DrawableRes icono: Int,
    descripcion: String,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(56.dp),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Icon(painterResource(icono), contentDescription = descripcion, modifier = Modifier.size(24.dp))
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
            onRegistrar = { _, _ -> },
            numeroSerie = 2,
            objetivoReps = "Objetivo: 6 reps"
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