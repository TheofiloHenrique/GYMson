package com.example.gymson.ui.perfil

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymson.data.FakeData.perfilDemo
import com.example.gymson.data.model.Equipamiento
import com.example.gymson.data.model.Nivel
import com.example.gymson.data.model.Objetivo
import com.example.gymson.ui.components.SelectorOpciones
import com.example.gymson.ui.theme.EntrenamientoPersonalTheme
import kotlin.math.roundToInt

// Texto para mostrar un enum: FUERZA -> "Fuerza"
private fun Enum<*>.etiqueta() = name.lowercase().replaceFirstChar { it.uppercase() }

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

//Versión sin ViewModel: solo estado y lambdas -> fácil de previsualizar
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
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(text = "Tu perfil", style = MaterialTheme.typography.headlineMedium)

        SelectorOpciones(
            titulo = "Objetivo",
            opciones = Objetivo.entries,
            seleccionada = uiState.objetivo,
            onSeleccion = onObjetivo,
            etiqueta = { it.etiqueta() }
        )

        SelectorOpciones(
            titulo = "Nivel de experiencia",
            opciones = Nivel.entries,
            seleccionada = uiState.nivel,
            onSeleccion = onNivel,
            etiqueta = { it.etiqueta() }
        )

        SelectorOpciones(
            titulo = "Días disponibles por semana",
            opciones = listOf(2, 3, 4, 5),
            seleccionada = uiState.diasPorSemana,
            onSeleccion = onDias
        )

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Duración de la sesión: ${uiState.minutosPorSesion} min",
                style = MaterialTheme.typography.titleMedium
            )
            // 20..90 de 5 en 5: hay 14 intervalos, o sea 13 "steps" intermedios
            Slider(
                value = uiState.minutosPorSesion.toFloat(),
                onValueChange = { onMinutos(it.roundToInt()) },
                valueRange = 20f..90f,
                steps = 13
            )
        }

        SelectorOpciones(
            titulo = "Equipamiento disponible",
            opciones = Equipamiento.entries,
            seleccionada = uiState.equipamiento,
            onSeleccion = onEquipamiento,
            etiqueta = { it.etiqueta() }
        )

        if (!uiState.puedeGuardar) {
            Text(
                text = "Elige objetivo, nivel y equipamiento para continuar.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = onGuardar,
            enabled = uiState.puedeGuardar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }
    }
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