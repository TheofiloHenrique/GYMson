package com.example.gymson.ui.catalogo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymson.data.model.Ejercicio
import com.example.gymson.data.repository.EjerciciosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Los tres estados posibles de la pantalla
sealed interface CatalogoUiState {
    data object Cargando : CatalogoUiState
    data class Exito(val ejercicios: List<Ejercicio>) : CatalogoUiState
    data class Error(val mensaje: String) : CatalogoUiState
}

class CatalogoViewModel : ViewModel() {

    // Privado y mutable dentro; público y de solo lectura fuera
    private val _uiState = MutableStateFlow<CatalogoUiState>(CatalogoUiState.Cargando)
    val uiState: StateFlow<CatalogoUiState> = _uiState.asStateFlow()

    init { cargar() } // al crearse el ViewModel se inicia la descarga

    fun cargar() {
        _uiState.value = CatalogoUiState.Cargando
        viewModelScope.launch {
            EjerciciosRepository.obtenerEjercicios()
                .onSuccess { _uiState.value = CatalogoUiState.Exito(it) }
                .onFailure {
                    _uiState.value = CatalogoUiState.Error(
                        "No se pudo descargar el catálogo. Revisa tu conexión a internet."
                    )
                }
        }
    }
}