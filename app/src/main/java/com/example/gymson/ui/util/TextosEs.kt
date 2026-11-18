package com.example.gymson.ui.util

// Términos fijos del catálogo (Free Exercise DB) traducidos al español.
// Es un diccionario estático: no necesita red ni librerías. La fase 7.10 lo amplía
// (nombres de ejercicios) y añade la traducción de instrucciones con ML Kit.

private fun String.capitalizar() = replaceFirstChar { it.uppercase() }

fun musculoEs(musculo: String): String = when (musculo) {
    "chest" -> "Pecho"
    "shoulders" -> "Hombros"
    "triceps" -> "Tríceps"
    "biceps" -> "Bíceps"
    "lats" -> "Dorsales"
    "middle back" -> "Espalda media"
    "traps" -> "Trapecios"
    "forearms" -> "Antebrazos"
    "quadriceps" -> "Cuádriceps"
    "hamstrings" -> "Isquiotibiales"
    "glutes" -> "Glúteos"
    "calves" -> "Pantorrillas"
    "adductors" -> "Aductores"
    "abductors" -> "Abductores"
    "abdominals" -> "Abdominales"
    "lower back" -> "Lumbares"
    "neck" -> "Cuello"
    else -> musculo.capitalizar()
}

// equipment puede venir null en el JSON: se trata como "body only" (igual que GeneradorRutina)
fun equipoEs(equipo: String?): String {
    val e = equipo ?: "body only"
    return when (e) {
        "body only" -> "Peso corporal"
        "dumbbell" -> "Mancuernas"
        "barbell" -> "Barra"
        "bands" -> "Bandas"
        "machine" -> "Máquina"
        "cable" -> "Polea"
        "kettlebells" -> "Pesas rusas"
        "medicine ball" -> "Balón medicinal"
        "exercise ball" -> "Balón de ejercicio"
        "foam roll" -> "Rodillo de espuma"
        "e-z curl bar" -> "Barra Z"
        "other" -> "Otro"
        else -> e.capitalizar()
    }
}

fun nivelEs(nivel: String): String = when (nivel) {
    "beginner" -> "Principiante"
    "intermediate" -> "Intermedio"
    "expert" -> "Avanzado"
    else -> nivel.capitalizar()
}

fun categoriaEs(categoria: String): String = when (categoria) {
    "strength" -> "Fuerza"
    "stretching" -> "Estiramiento"
    "plyometrics" -> "Pliométrico"
    "cardio" -> "Cardio"
    "powerlifting" -> "Powerlifting"
    "olympic weightlifting" -> "Halterofilia"
    "strongman" -> "Strongman"
    else -> categoria.capitalizar()
}
