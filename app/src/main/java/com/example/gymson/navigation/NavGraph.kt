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
import com.example.gymson.ui.rutina.RutinaScreen
import com.example.gymson.ui.sesion.SesionScreen

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
            PerfilScreen(onGuardado = { navController.navegarEnBarra(Rutas.RUTINA) })
        }
        composable(Rutas.RUTINA) {
            RutinaScreen(
                onComenzar = { navController.navigate(Rutas.SESION) },
                onIrAPerfil = { navController.navegarEnBarra(Rutas.PERFIL) }
            )
        }
        composable(Rutas.SESION) {
            SesionScreen(onTerminar = { navController.popBackStack() })
        }
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

//Misma navegación que la barra inferior: evita apilar pantallas repetidas
private fun NavHostController.navegarEnBarra(ruta: String) {
    navigate(ruta) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}