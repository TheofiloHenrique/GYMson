package com.example.gymson.data

import com.example.gymson.data.model.Ejercicio
import com.example.gymson.data.model.EjercicioRutina
import com.example.gymson.data.model.Equipamiento
import com.example.gymson.data.model.Nivel
import com.example.gymson.data.model.Objetivo
import com.example.gymson.data.model.PerfilUsuario
import com.example.gymson.data.model.RegistroSerie
import com.example.gymson.data.model.Rutina
import com.example.gymson.data.model.SesionCompletada

//Datos escritos a mano para los @Preview y para avanzar sin esperar los otros pasos
object FakeData {

    val perfilDemo = PerfilUsuario(
        objetivo = Objetivo.FUERZA,
        nivel = Nivel.PRINCIPIANTE,
        diasPorSemana = 3,
        minutosPorSesion = 40,
        equipamiento = Equipamiento.NINGUNO
    )

    val ejerciciosDemo = listOf(
        Ejercicio(
            id = "Pushups",
            name = "Pushups",
            level = "beginner",
            equipment = "body only",
            category = "strength",
            primaryMuscles = listOf("chest"),
            instructions = listOf("Start in plank position.", "Lower your chest and push back up."),
            images = listOf("Pushups/0.jpg", "Pushups/1.jpg")
        ),
        Ejercicio(
            id = "Bodyweight_Squat",
            name = "Bodyweight Squat",
            level = "beginner",
            equipment = "body only",
            category = "strength",
            primaryMuscles = listOf("quadriceps"),
            instructions = listOf(
                "Stand with your feet shoulder-width apart.",
                "Lower your hips back and down, then push through your heels to stand."
            ),
            images = listOf("Bodyweight_Squat/0.jpg", "Bodyweight_Squat/1.jpg")
        ),
        Ejercicio(
            id = "Plank",
            name = "Plank",
            level = "beginner",
            equipment = "body only",
            category = "strength",
            primaryMuscles = listOf("abdominals"),
            instructions = listOf(
                "Get into a pushup position on your forearms.",
                "Keep your body in a straight line and hold the position."
            ),
            images = listOf("Plank/0.jpg", "Plank/1.jpg")
        )
    )

    val rutinaDemo = Rutina(
        nombre = "Cuerpo completo",
        ejercicios = ejerciciosDemo.map {
            EjercicioRutina(ejercicio = it, series = 4, repeticiones = 6, descansoSegundos = 90)
        }
    )

    val sesionesDemo = listOf(
        SesionCompletada(
            fechaMillis = 1_730_000_000_000L,
            nombreRutina = "Tren superior",
            duracionMinutos = 38,
            series = listOf(
                RegistroSerie("Pushups", 1, 10, 0f),
                RegistroSerie("Pushups", 2, 8, 0f)
            )
        ),
        SesionCompletada(
            fechaMillis = 1_730_100_000_000L,
            nombreRutina = "Tren inferior",
            duracionMinutos = 42,
            series = listOf(
                RegistroSerie("Bodyweight_Squat", 1, 15, 0f),
                RegistroSerie("Bodyweight_Squat", 2, 12, 0f)
            )
        )
    )
}