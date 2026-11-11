package com.example.gymson.ui.perfil

import androidx.lifecycle.ViewModel
import com.example.gymson.data.model.Equipamiento
import com.example.gymson.data.model.Nivel
import com.example.gymson.data.model.Objetivo
import com.example.gymson.data.model.PerfilUsuario
import com.example.gymson.data.repository.PerfilRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

//Estado del formulario. Los campos obligatorios son null hasta que el usuario elige.
data class PerfilUiState(
    val objetivo: Objetivo? = null,
    val nivel: Nivel? = null,
    val diasPorSemana: Int = 3,
    val minutosPorSesion: Int = 40,
    val equipamiento: Equipamiento? = null
) {
    //Criterio de aceptación RF01: no se guarda con campos obligatorios vacíos
    val puedeGuardar: Boolean
        get() = objetivo != null && nivel != null && equipamiento != null
}

// Convierte un perfil guardado en estado del formulario (precarga para editar, RF08)
fun PerfilUsuario.toUiState() = PerfilUiState(
    objetivo = objetivo,
    nivel = nivel,
    diasPorSemana = diasPorSemana,
    minutosPorSesion = minutosPorSesion,
    equipamiento = equipamiento
)

class PerfilViewModel : ViewModel() {

    //Si ya hay perfil se precarga; si no, el formulario empieza vacío
    private val _uiState = MutableStateFlow(
        PerfilRepository.perfil.value?.toUiState() ?: PerfilUiState()
    )
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    fun onObjetivoChange(valor: Objetivo) = _uiState.update { it.copy(objetivo = valor) }
    fun onNivelChange(valor: Nivel) = _uiState.update { it.copy(nivel = valor) }
    fun onDiasChange(valor: Int) = _uiState.update { it.copy(diasPorSemana = valor) }
    fun onMinutosChange(valor: Int) = _uiState.update { it.copy(minutosPorSesion = valor) }
    fun onEquipamientoChange(valor: Equipamiento) = _uiState.update { it.copy(equipamiento = valor) }

    //Guarda en el repositorio y avisa a la pantalla para que navegue
    fun guardar(onGuardado: () -> Unit) {
        val s = _uiState.value
        val objetivo = s.objetivo ?: return
        val nivel = s.nivel ?: return
        val equipamiento = s.equipamiento ?: return

        PerfilRepository.guardar(
            PerfilUsuario(
                objetivo = objetivo,
                nivel = nivel,
                diasPorSemana = s.diasPorSemana,
                minutosPorSesion = s.minutosPorSesion,
                equipamiento = equipamiento
            )
        )
        onGuardado()
    }
}