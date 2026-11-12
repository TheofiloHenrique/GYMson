package com.example.gymson.domain

import com.example.gymson.data.model.Ejercicio
import com.example.gymson.data.model.EjercicioRutina
import com.example.gymson.data.model.Equipamiento
import com.example.gymson.data.model.Nivel
import com.example.gymson.data.model.Objetivo
import com.example.gymson.data.model.PerfilUsuario
import com.example.gymson.data.model.Rutina
import java.time.LocalDate
import kotlin.random.Random

private val MUSCULOS_SUPERIOR =
    setOf("chest", "shoulders", "triceps", "biceps", "lats", "middle back", "traps", "forearms")
private val MUSCULOS_INFERIOR =
    setOf("quadriceps", "hamstrings", "glutes", "calves", "adductors", "abductors")
private val MUSCULOS_CORE = setOf("abdominals", "lower back")
private val MUSCULOS_COMPLETO = MUSCULOS_SUPERIOR + MUSCULOS_INFERIOR + MUSCULOS_CORE

// Una "división" = nombre de la sesión + músculos que se trabajan ese día
private data class Division(val nombre: String, val musculos: Set<String>)

private val SUPERIOR = Division("Tren superior", MUSCULOS_SUPERIOR)
private val INFERIOR = Division("Tren inferior", MUSCULOS_INFERIOR)
private val CORE = Division("Core", MUSCULOS_CORE)
private val COMPLETO = Division("Cuerpo completo", MUSCULOS_COMPLETO)

private fun divisionesPara(diasPorSemana: Int): List<Division> = when (diasPorSemana) {
    2 -> listOf(
        Division("Cuerpo completo A", MUSCULOS_COMPLETO),
        Division("Cuerpo completo B", MUSCULOS_COMPLETO)
    )
    3 -> listOf(SUPERIOR, INFERIOR, COMPLETO)
    4 -> listOf(SUPERIOR, INFERIOR, SUPERIOR, INFERIOR)
    else -> listOf(SUPERIOR, INFERIOR, CORE, SUPERIOR, INFERIOR) // 5 días
}

// Niveles del JSON permitidos según la experiencia declarada (nunca "expert")
private fun nivelesPermitidos(nivel: Nivel): Set<String> = when (nivel) {
    Nivel.PRINCIPIANTE -> setOf("beginner")
    Nivel.INTERMEDIO -> setOf("beginner", "intermediate")
}

// Equipos del JSON permitidos. null = sin restricción (gimnasio).
// En el JSON "equipment" puede venir null; se trata como "body only".
private fun equiposPermitidos(equipamiento: Equipamiento): Set<String>? = when (equipamiento) {
    Equipamiento.NINGUNO -> setOf("body only")
    Equipamiento.MANCUERNAS -> setOf("body only", "dumbbell")
    Equipamiento.BARRA -> setOf("body only", "dumbbell", "barbell")
    Equipamiento.BANDAS -> setOf("body only", "bands")
    Equipamiento.GIMNASIO -> null
}

private fun categoriasPermitidas(objetivo: Objetivo): Set<String> = when (objetivo) {
    Objetivo.FUERZA -> setOf("strength")
    Objetivo.RESISTENCIA -> setOf("strength", "plyometrics", "cardio")
    Objetivo.FLEXIBILIDAD -> setOf("stretching")
}

private data class Parametros(val series: Int, val repeticiones: Int, val descansoSegundos: Int)

private fun parametrosPara(objetivo: Objetivo) = when (objetivo) {
    Objetivo.FUERZA -> Parametros(series = 4, repeticiones = 6, descansoSegundos = 90)
    Objetivo.RESISTENCIA -> Parametros(series = 3, repeticiones = 15, descansoSegundos = 45)
    Objetivo.FLEXIBILIDAD -> Parametros(series = 2, repeticiones = 30, descansoSegundos = 20) // 30 = segundos
}

// ---------- Función principal ----------

/**
 * Función pura: mismos datos de entrada -> misma rutina. Por eso es fácil de probar y de explicar.
 * [diasDesdeEpoch] solo se usa como semilla del orden aleatorio; tiene valor por defecto (hoy)
 * y se puede fijar en pruebas.
 */
fun generarRutina(
    perfil: PerfilUsuario,
    catalogo: List<Ejercicio>,
    numeroSesion: Int,
    diasDesdeEpoch: Long = LocalDate.now().toEpochDay()
): Rutina {
    // Paso 1: qué sesión toca (rota entre las divisiones de la semana)
    val divisiones = divisionesPara(perfil.diasPorSemana)
    val division = divisiones[numeroSesion % divisiones.size]

    // Paso 2: filtros (todos deben cumplirse)
    val niveles = nivelesPermitidos(perfil.nivel)
    val equipos = equiposPermitidos(perfil.equipamiento)
    val categorias = categoriasPermitidas(perfil.objetivo)

    val candidatos = catalogo.filter { ejercicio ->
        ejercicio.level in niveles &&
                (equipos == null || (ejercicio.equipment ?: "body only") in equipos) &&
                ejercicio.category in categorias &&
                ejercicio.primaryMuscles.any { it in division.musculos }
    }

    // Paso 3: cantidad (≈ 8 min por ejercicio), entre 3 y 8
    val cantidad = (perfil.minutosPorSesion / 8).coerceIn(3, 8)

    // Paso 4: semilla estable -> la rutina no cambia al reabrir la app el mismo día
    val seleccion = candidatos.shuffled(Random(diasDesdeEpoch + numeroSesion)).take(cantidad)

    // Paso 5: series, repeticiones y descanso según el objetivo
    val p = parametrosPara(perfil.objetivo)
    return Rutina(
        nombre = division.nombre,
        ejercicios = seleccion.map {
            EjercicioRutina(
                ejercicio = it,
                series = p.series,
                repeticiones = p.repeticiones,
                descansoSegundos = p.descansoSegundos
            )
        }
    )
}