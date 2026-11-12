package com.example.gymson.ui.sesion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymson.data.model.EjercicioRutina
import com.example.gymson.data.model.Rutina
import com.example.gymson.data.repository.RutinaRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SesionUiState(
    val rutina: Rutina? = null,
    val indiceEjercicio: Int = 0,
    val serieActual: Int = 1,
    val descansoRestante: Int = 0,
    val descansando: Boolean = false,
    val descansoTerminado: Boolean = false
) {
    val ejercicioRutina: EjercicioRutina?
        get() = rutina?.ejercicios?.getOrNull(indiceEjercicio)
    val totalEjercicios: Int
        get() = rutina?.ejercicios?.size ?: 0
    val esUltimaSerie: Boolean
        get() = ejercicioRutina?.let { serieActual >= it.series } ?: true
    val esUltimoEjercicio: Boolean
        get() = indiceEjercicio >= totalEjercicios - 1
}

class SesionViewModel : ViewModel() {

    //Copia única: la Etapa 5 sobrescribe rutinaActual al guardar la sesión.
    private val _uiState = MutableStateFlow(
        SesionUiState(rutina = RutinaRepository.rutinaActual.value)
    )
    val uiState: StateFlow<SesionUiState> = _uiState.asStateFlow()

    private var temporizador: Job? = null

    fun iniciarDescanso() {
        val segundos = _uiState.value.ejercicioRutina?.descansoSegundos ?: return
        temporizador?.cancel() // evita dos cuentas regresivas a la vez
        _uiState.update {
            it.copy(descansando = true, descansoTerminado = false, descansoRestante = segundos)
        }
        temporizador = viewModelScope.launch {
            while (_uiState.value.descansoRestante > 0) {
                delay(1000)
                _uiState.update { it.copy(descansoRestante = it.descansoRestante - 1) }
            }
            _uiState.update { it.copy(descansando = false, descansoTerminado = true) }
        }
    }

    fun saltarDescanso() {
        detenerDescanso()
    }

    /** Avanza a la siguiente serie, al siguiente ejercicio o termina la sesión. */
    fun avanzar(onTerminar: () -> Unit) {
        val estado = _uiState.value
        detenerDescanso()
        when {
            !estado.esUltimaSerie ->
                _uiState.update { it.copy(serieActual = it.serieActual + 1) }
            !estado.esUltimoEjercicio ->
                _uiState.update { it.copy(indiceEjercicio = it.indiceEjercicio + 1, serieActual = 1) }
            else -> onTerminar() // TODO Etapa 5: armar SesionCompletada y llamar a HistorialRepository.agregar(...) antes
        }
    }

    private fun detenerDescanso() {
        temporizador?.cancel()
        _uiState.update {
            it.copy(descansando = false, descansoTerminado = false, descansoRestante = 0)
        }
    }
}