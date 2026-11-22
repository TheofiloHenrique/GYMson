package com.example.gymson.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gymson.ui.catalogo.CatalogoScreen
import com.example.gymson.ui.historial.HistorialScreen
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
            SesionScreen(
                onTerminar = { navController.popBackStack() },
                onVerHistorial = {
                    // Primero se sale de la sesión y luego se va al historial por la barra
                    navController.popBackStack()
                    navController.navegarEnBarra(Rutas.HISTORIAL)
                }
            )
        }
        composable(Rutas.HISTORIAL) { HistorialScreen() }
        composable(Rutas.CATALOGO) { CatalogoScreen() }
    }
}

private fun NavHostController.navegarEnBarra(ruta: String) {
    navigate(ruta) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}