package com.example.gymson.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Singleton: una sola instancia de Retrofit para toda la app
object RetrofitClient {
    private const val BASE_URL = "https://raw.githubusercontent.com/" // debe terminar en "/"

    // "by lazy": se crea la primera vez que se usa, no al abrir la app
    val api: EjerciciosApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // JSON -> data class
            .build()
            .create(EjerciciosApi::class.java)
    }
}