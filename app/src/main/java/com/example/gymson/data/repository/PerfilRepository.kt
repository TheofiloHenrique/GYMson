package com.example.gymson.data.repository

import com.example.gymson.data.model.PerfilUsuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

//"object" = singleton: todos los ViewModels comparten la misma instancia
object PerfilRepository {
    private val _perfil = MutableStateFlow<PerfilUsuario?>(null)
    val perfil: StateFlow<PerfilUsuario?> = _perfil.asStateFlow()

    fun guardar(perfil: PerfilUsuario) { _perfil.value = perfil }
}