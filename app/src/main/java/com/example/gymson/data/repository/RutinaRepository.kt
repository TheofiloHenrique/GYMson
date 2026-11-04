package com.example.gymson.data.repository

import com.example.gymson.data.model.Rutina
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object RutinaRepository {
    private val _rutinaActual = MutableStateFlow<Rutina?>(null)
    val rutinaActual: StateFlow<Rutina?> = _rutinaActual.asStateFlow()

    fun establecer(rutina: Rutina) { _rutinaActual.value = rutina }
}