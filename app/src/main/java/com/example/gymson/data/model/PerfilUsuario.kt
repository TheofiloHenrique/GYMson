package com.example.gymson.data.model

enum class Objetivo { FUERZA, RESISTENCIA, FLEXIBILIDAD }
enum class Nivel { PRINCIPIANTE, INTERMEDIO }
enum class Equipamiento { NINGUNO, MANCUERNAS, BARRA, BANDAS, GIMNASIO }

data class PerfilUsuario(
    val objetivo: Objetivo,
    val nivel: Nivel,
    val diasPorSemana: Int,
    val minutosPorSesion: Int,
    val equipamiento: Equipamiento
)