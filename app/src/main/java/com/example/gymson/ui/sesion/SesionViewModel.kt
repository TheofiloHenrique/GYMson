package com.example.gymson.ui.sesion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymson.data.model.EjercicioRutina
import com.example.gymson.data.model.RegistroSerie
import com.example.gymson.data.model.Rutina
import com.example.gymson.data.model.SesionCompletada
import com.example.gymson.data.repository.HistorialRepository
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
    val descansoTerminado: Boolean = false,
    val registros: List<RegistroSerie> = emptyList(), // Etapa 5: series registradas en esta sesión
    val ultimoPorEjercicio: Map<String, RegistroSerie> = emptyMap() // / Última serie registrada de cada ejercicio en el historial (para mostrar "Anterior: X kg × Y reps")
) {
    val ejercicioRutina: EjercicioRutina?
        get() = rutina?.ejercicios?.getOrNull(indiceEjercicio)
    val totalEjercicios: Int
        get() = rutina?.ejercicios?.size ?: 0
    val esUltimaSerie: Boolean
        get() = ejercicioRutina?.let { serieActual >= it.series } ?: true
    val esUltimoEjercicio: Boolean
        get() = indiceEjercicio >= totalEjercicios - 1

    // ¿Ya se registró la serie que se está mostrando?
    val serieRegistrada: Boolean
        get() = ejercicioRutina?.let { actual ->
            registros.any { it.ejercicioId == actual.ejercicio.id && it.numeroSerie == serieActual }
        } ?: false

    // Última carga usada en este ejercicio: se sugiere en la siguiente serie para ahorrar tecleo
    val cargaSugerida: Float?
        get() = ejercicioRutina?.let { actual ->
            registros.lastOrNull { it.ejercicioId == actual.ejercicio.id }?.cargaKg
        }

    val anterior: RegistroSerie?
        get() = ejercicioRutina?.let { ultimoPorEjercicio[it.ejercicio.id] }
}

class SesionViewModel : ViewModel() {

    //Copia única: la Etapa 5 sobrescribe rutinaActual al guardar la sesión.
    private val _uiState = MutableStateFlow(
        SesionUiState(
            rutina = RutinaRepository.rutinaActual.value,
            ultimoPorEjercicio = ultimosRegistros()
        )
    )

    // El historial está en orden cronológico: las series más recientes sobrescriben a las anteriores
    private fun ultimosRegistros(): Map<String, RegistroSerie> {
        val resultado = mutableMapOf<String, RegistroSerie>()
        HistorialRepository.sesiones.value.forEach { sesion ->
            sesion.series.forEach { resultado[it.ejercicioId] = it }
        }
        return resultado
    }

    val uiState: StateFlow<SesionUiState> = _uiState.asStateFlow()

    private var temporizador: Job? = null

    // Hora de inicio: sirve para calcular la duración al terminar (SesionCompletada.duracionMinutos)
    private val inicioMillis = System.currentTimeMillis()

    // Evita guardar la sesión dos veces si se pulsa «Terminar sesión» dos veces seguidas
    private var sesionGuardada = false

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

    /** RF05: guarda las repeticiones y la carga de la serie que se está mostrando. */
    fun registrarSerie(repeticiones: Int, cargaKg: Float) {
        _uiState.update { estado ->
            val actual = estado.ejercicioRutina ?: return@update estado
            val nuevo = RegistroSerie(
                ejercicioId = actual.ejercicio.id,
                numeroSerie = estado.serieActual,
                repeticiones = repeticiones,
                cargaKg = cargaKg
            )
            // Si la serie ya estaba registrada, se reemplaza (permite corregir un dato)
            val sinRepetido = estado.registros.filterNot {
                it.ejercicioId == nuevo.ejercicioId && it.numeroSerie == nuevo.numeroSerie
            }
            estado.copy(registros = sinRepetido + nuevo)
        }
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
            else -> terminarSesion(estado, onTerminar)
        }
    }

    // RF06: arma la SesionCompletada y la guarda en el historial.
    // Se llama JUSTO ANTES de salir de la pantalla: al agregar al historial, RutinaViewModel
    // regenera la rutina del siguiente día y sobrescribe rutinaActual; esta pantalla ya no la necesita.
    private fun terminarSesion(estado: SesionUiState, onTerminar: () -> Unit) {
        if (sesionGuardada) return
        sesionGuardada = true

        val ahora = System.currentTimeMillis()
        HistorialRepository.agregar(
            SesionCompletada(
                fechaMillis = ahora,
                nombreRutina = estado.rutina?.nombre ?: "Sesión",
                duracionMinutos = ((ahora - inicioMillis) / 60_000).toInt().coerceAtLeast(1),
                series = estado.registros
            )
        )
        onTerminar()
    }

    private fun detenerDescanso() {
        temporizador?.cancel()
        _uiState.update {
            it.copy(descansando = false, descansoTerminado = false, descansoRestante = 0)
        }
    }
}