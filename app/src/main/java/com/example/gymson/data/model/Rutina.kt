package com.example.gymson.data.model

data class EjercicioRutina(
    val ejercicio: Ejercicio,
    val series: Int,
    val repeticiones: Int,         // en FLEXIBILIDAD se interpreta como segundos
    val descansoSegundos: Int
)

data class Rutina(
    val nombre: String,
    val ejercicios: List<EjercicioRutina>
)