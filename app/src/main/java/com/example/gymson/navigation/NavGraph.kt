package com.example.gymson.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gymson.ui.catalogo.CatalogoScreen
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.gymson.ui.perfil.PerfilScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        //Cada etapa va a reemplazar el Placeholder por su pantalla real
        composable(Rutas.PERFIL) {
            PerfilScreen(
                onGuardado = {
                    // Mismo patrón que la barra inferior: evita apilar pantallas repetidas
                    navController.navigate(Rutas.RUTINA) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }       // Etapa 2
        composable(Rutas.RUTINA) { Placeholder("Rutina") }        // Etapa 3
        composable(Rutas.SESION) { Placeholder("Sesión") }        // Etapas 4 y 5
        composable(Rutas.HISTORIAL) { Placeholder("Historial") }  // Etapa 5
        composable(Rutas.CATALOGO) { CatalogoScreen() }
        // composable(Rutas.CATALOGO) { ... }                     // Etapa 1
    }
}
@Composable
private fun Placeholder(nombre: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(nombre)
    }
}