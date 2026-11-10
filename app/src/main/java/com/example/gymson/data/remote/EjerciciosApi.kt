package com.example.gymson.data.remote

import com.example.gymson.data.model.Ejercicio
import retrofit2.http.GET

// Interfaz Retrofit: describe el endpoint. Retrofit genera la implementación.
interface EjerciciosApi {
    // Se concatena con la BASE_URL de RetrofitClient
    @GET("yuhonas/free-exercise-db/main/dist/exercises.json")
    suspend fun obtenerEjercicios(): List<Ejercicio>
}