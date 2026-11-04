package com.example.gymson.data.model

data class RegistroSerie(
    val ejercicioId: String,
    val numeroSerie: Int,
    val repeticiones: Int,
    val cargaKg: Float
)

data class SesionCompletada(
    val fechaMillis: Long,
    val nombreRutina: String,
    val duracionMinutos: Int,
    val series: List<RegistroSerie>
)