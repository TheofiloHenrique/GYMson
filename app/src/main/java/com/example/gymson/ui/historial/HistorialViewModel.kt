package com.example.gymson.ui.historial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymson.data.model.SesionCompletada
import com.example.gymson.data.repository.HistorialRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

// Estado de la pantalla: las sesiones ya vienen ordenadas (la más reciente primero)
data class HistorialUiState(
    val sesiones: List<SesionCompletada> = emptyList()
) {
    val totalSesiones: Int
        get() = sesiones.size
}

// Volumen de una sesión = Σ repeticiones × carga (RF07).
// Es una función de extensión aquí para no modificar el archivo contrato Registro.kt.
fun SesionCompletada.volumenKg(): Float =
    series.sumOf { (it.repeticiones * it.cargaKg).toDouble() }.toFloat()

class HistorialViewModel : ViewModel() {

    // El repositorio guarda las sesiones en orden cronológico; aquí se invierte para mostrarlas.
    // stateIn convierte el Flow del repositorio en un StateFlow que la pantalla puede observar.
    val uiState: StateFlow<HistorialUiState> = HistorialRepository.sesiones
        .map { HistorialUiState(it.asReversed()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            // valor inicial real (no vacío) para que no parpadee el mensaje «sin sesiones»
            initialValue = HistorialUiState(HistorialRepository.sesiones.value.asReversed())
        )
}