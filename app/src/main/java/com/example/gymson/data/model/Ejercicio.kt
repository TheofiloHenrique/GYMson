package com.example.gymson.data.model

//DTO
data class Ejercicio(
    val id: String,
    val name: String,
    val level: String,
    val equipment: String?,          //puede venir null
    val category: String,
    val primaryMuscles: List<String>,
    val instructions: List<String>,
    val images: List<String>
) {

    fun urlImagen(): String? = images.firstOrNull()?.let {
        "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/$it"
    }
}