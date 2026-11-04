package com.example.gymson.data.repository

import com.example.gymson.data.model.SesionCompletada
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object HistorialRepository {
    private val _sesiones = MutableStateFlow<List<SesionCompletada>>(emptyList())
    val sesiones: StateFlow<List<SesionCompletada>> = _sesiones.asStateFlow()

    //Creamos una lista nueva (no modificamos la antigua) para que el StateFlow emita el cambio
    fun agregar(sesion: SesionCompletada) { _sesiones.value = _sesiones.value + sesion }
}