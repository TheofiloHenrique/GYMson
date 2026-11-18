package com.example.gymson.ui.perfil

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymson.R
import com.example.gymson.data.FakeData.perfilDemo
import com.example.gymson.data.model.Equipamiento
import com.example.gymson.data.model.Nivel
import com.example.gymson.data.model.Objetivo
import com.example.gymson.ui.components.BotonSecundario
import com.example.gymson.ui.components.SelectorOpciones
import com.example.gymson.ui.components.TarjetaGymson
import com.example.gymson.ui.components.aparecerEscalonado
import com.example.gymson.ui.theme.EntrenamientoPersonalTheme
import kotlin.math.roundToInt

// Texto para mostrar un enum: FUERZA -> "Fuerza"
private fun Enum<*>.etiqueta() = name.lowercase().replaceFirstChar { it.uppercase() }

// Ícono de cada opción (los chips del mockup llevan ícono)
@DrawableRes
private fun iconoObjetivo(o: Objetivo) = when (o) {
    Objetivo.FUERZA -> R.drawable.ic_bolt
    Objetivo.RESISTENCIA -> R.drawable.ic_directions_run
    Objetivo.FLEXIBILIDAD -> R.drawable.ic_self_improvement
}

@DrawableRes
private fun iconoNivel(n: Nivel) = when (n) {
    Nivel.PRINCIPIANTE -> R.drawable.ic_lightbulb
    Nivel.INTERMEDIO -> R.drawable.ic_fitness_center
}

@DrawableRes
private fun iconoEquipamiento(e: Equipamiento) = when (e) {
    Equipamiento.NINGUNO -> R.drawable.ic_block
    Equipamiento.MANCUERNAS -> R.drawable.ic_fitness_center
    Equipamiento.BARRA -> R.drawable.ic_remove
    Equipamiento.BANDAS -> R.drawable.ic_all_inclusive
    Equipamiento.GIMNASIO -> R.drawable.ic_domain
}

// Versión con ViewModel (la que usa el NavGraph)
@Composable
fun PerfilScreen(
    onGuardado: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PerfilViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    PerfilContent(
        uiState = uiState,
        onObjetivo = viewModel::onObjetivoChange,
        onNivel = viewModel::onNivelChange,
        onDias = viewModel::onDiasChange,
        onMinutos = viewModel::onMinutosChange,
        onEquipamiento = viewModel::onEquipamientoChange,
        onGuardar = { viewModel.guardar(onGuardado) },
        modifier = modifier
    )
}

// Versión sin ViewModel: solo estado y lambdas -> fácil de previsualizar
@Composable
fun PerfilContent(
    uiState: PerfilUiState,
    onObjetivo: (Objetivo) -> Unit,
    onNivel: (Nivel) -> Unit,
    onDias: (Int) -> Unit,
    onMinutos: (Int) -> Unit,
    onEquipamiento: (Equipamiento) -> Unit,
    onGuardar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState()) // el formulario puede no caber en pantallas pequeñas
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TarjetaBienvenida(Modifier.aparecerEscalonado(0))

        SeccionPerfil(1, "¿Cuál es tu objetivo principal?", R.drawable.ic_target, indice = 1) {
            SelectorOpciones(
                opciones = Objetivo.entries,
                seleccionada = uiState.objetivo,
                onSeleccion = onObjetivo,
                etiqueta = { it.etiqueta() },
                icono = { iconoObjetivo(it) }
            )
        }

        SeccionPerfil(2, "Tu nivel de experiencia", R.drawable.ic_trending_up, indice = 2) {
            SelectorOpciones(
                opciones = Nivel.entries,
                seleccionada = uiState.nivel,
                onSeleccion = onNivel,
                etiqueta = { it.etiqueta() },
                icono = { iconoNivel(it) }
            )
        }

        SeccionPerfil(3, "Días por semana", R.drawable.ic_calendar_month, indice = 3) {
            SelectorOpciones(
                opciones = listOf(2, 3, 4, 5),
                seleccionada = uiState.diasPorSemana,
                onSeleccion = onDias,
                etiqueta = { "$it días" }
            )
        }

        SeccionPerfil(4, "Equipamiento disponible", R.drawable.ic_inventory_2, indice = 4) {
            SelectorOpciones(
                opciones = Equipamiento.entries,
                seleccionada = uiState.equipamiento,
                onSeleccion = onEquipamiento,
                etiqueta = { it.etiqueta() },
                icono = { iconoEquipamiento(it) }
            )
        }

        SeccionPerfil(
            numero = 5,
            titulo = "Duración de la sesión",
            icono = R.drawable.ic_timer,
            indice = 5,
            acento = { PildoraMinutos(uiState.minutosPorSesion) }
        ) {
            Column {
                // 20..90 de 5 en 5: hay 14 intervalos, o sea 13 "steps" intermedios
                Slider(
                    value = uiState.minutosPorSesion.toFloat(),
                    onValueChange = { onMinutos(it.roundToInt()) },
                    valueRange = 20f..90f,
                    steps = 13,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        activeTickColor = Color.Transparent,   // sin puntitos de los pasos
                        inactiveTickColor = Color.Transparent
                    )
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    EtiquetaEscala("20 min (rápido)")
                    EtiquetaEscala("60 min")
                    EtiquetaEscala("90 min (completo)")
                }
            }
        }

        if (!uiState.puedeGuardar) {
            Text(
                text = "Elige objetivo, nivel y equipamiento para continuar.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }

        BotonSecundario(
            texto = "Guardar perfil",
            onClick = onGuardar,
            enabled = uiState.puedeGuardar,
            modifier = Modifier.fillMaxWidth(),
            icono = R.drawable.ic_check
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_favorite),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "  Podrás ajustar tus preferencias en cualquier momento",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TarjetaBienvenida(modifier: Modifier = Modifier) {
    TarjetaGymson(
        modifier = modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_fitness_center),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(26.dp)
                )
            }
            Column {
                Text(
                    "Cuéntanos sobre ti",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    "Solo 5 datos para armar tu rutina",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

// Tarjeta de sección: ícono + "N. título" (+ un elemento opcional a la derecha) y el contenido
@Composable
private fun SeccionPerfil(
    numero: Int,
    titulo: String,
    @DrawableRes icono: Int,
    indice: Int,
    modifier: Modifier = Modifier,
    acento: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    TarjetaGymson(modifier = modifier.fillMaxWidth().aparecerEscalonado(indice)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(icono),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "$numero. $titulo",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                acento?.invoke()
            }
            content()
        }
    }
}

// Valor del slider en una píldora. "tnum": cifras de ancho fijo, no baila al deslizar.
@Composable
private fun PildoraMinutos(minutos: Int) {
    Text(
        text = "$minutos min",
        style = MaterialTheme.typography.titleMedium.copy(fontFeatureSettings = "tnum"),
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    )
}

@Composable
private fun EtiquetaEscala(texto: String) {
    Text(
        text = texto,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Preview(showBackground = true)
@Composable
private fun PerfilPreview() {
    EntrenamientoPersonalTheme {
        PerfilContent(
            uiState = perfilDemo.toUiState(),
            onObjetivo = {}, onNivel = {}, onDias = {},
            onMinutos = {}, onEquipamiento = {}, onGuardar = {}
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PerfilVacioOscuroPreview() {
    EntrenamientoPersonalTheme {
        PerfilContent(
            uiState = PerfilUiState(),
            onObjetivo = {}, onNivel = {}, onDias = {},
            onMinutos = {}, onEquipamiento = {}, onGuardar = {}
        )
    }
}
