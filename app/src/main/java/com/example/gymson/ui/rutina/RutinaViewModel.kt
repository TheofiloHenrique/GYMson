package com.example.gymson.ui.rutina

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymson.data.model.Rutina
import com.example.gymson.data.repository.EjerciciosRepository
import com.example.gymson.data.repository.HistorialRepository
import com.example.gymson.data.repository.PerfilRepository
import com.example.gymson.data.repository.RutinaRepository
import com.example.gymson.domain.generarRutina
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

// Los cuatro estados posibles de la pantalla
sealed interface RutinaUiState {
    data object SinPerfil : RutinaUiState
    data object Cargando : RutinaUiState
    data class Error(val mensaje: String) : RutinaUiState
    data class Exito(val rutina: Rutina) : RutinaUiState
}

class RutinaViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<RutinaUiState>(RutinaUiState.Cargando)
    val uiState: StateFlow<RutinaUiState> = _uiState.asStateFlow()

    private var job: Job? = null

    init { cargar() }

    // También se usa para "Reintentar": cancela la observación anterior y empieza de nuevo
    fun cargar() {
        job?.cancel()
        job = viewModelScope.launch {
            // Se vuelve a generar la rutina cuando cambia el perfil (edición) o el historial
            // (el número de sesiones completadas decide qué día del ciclo toca)
            combine(PerfilRepository.perfil, HistorialRepository.sesiones) { perfil, sesiones ->
                perfil to sesiones.size
            }.collectLatest { (perfil, sesionesCompletadas) ->
                if (perfil == null) {
                    _uiState.value = RutinaUiState.SinPerfil
                    return@collectLatest
                }
                _uiState.value = RutinaUiState.Cargando
                EjerciciosRepository.obtenerEjercicios()
                    .onSuccess { catalogo ->
                        val rutina = generarRutina(perfil, catalogo, sesionesCompletadas)
                        RutinaRepository.establecer(rutina) // la Etapa 4 la lee de aquí
                        _uiState.value = RutinaUiState.Exito(rutina)
                    }
                    .onFailure {
                        _uiState.value = RutinaUiState.Error(
                            "No se pudo descargar el catálogo. Revisa tu conexión a internet."
                        )
                    }
            }
        }
    }
}