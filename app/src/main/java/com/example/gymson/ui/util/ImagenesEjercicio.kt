package com.example.gymson.ui.util

import com.example.gymson.data.model.Ejercicio

private const val BASE_IMAGENES =
    "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/"

// Los 2 cuadros del ejercicio (0.jpg y 1.jpg). Es una función de extensión para no
// modificar el archivo contrato Ejercicio.kt. Si el ejercicio tiene una sola imagen, la lista tiene 1 elemento.
fun Ejercicio.urlsImagenes(): List<String> = images.take(2).map { BASE_IMAGENES + it }