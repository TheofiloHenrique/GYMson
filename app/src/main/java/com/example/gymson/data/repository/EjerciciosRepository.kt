package com.example.gymson.data.repository

import com.example.gymson.data.model.Ejercicio
import com.example.gymson.data.remote.RetrofitClient

// Descarga el catálogo una sola vez y lo guarda en memoria (caché).
object EjerciciosRepository {
    private var cache: List<Ejercicio>? = null

    // Result evita lanzar excepciones hacia el ViewModel: devuelve éxito o fallo.
    suspend fun obtenerEjercicios(forzarDescarga: Boolean = false): Result<List<Ejercicio>> {
        val enCache = cache
        if (enCache != null && !forzarDescarga) return Result.success(enCache)

        return runCatching { RetrofitClient.api.obtenerEjercicios() }
            .onSuccess { cache = it }
    }
}