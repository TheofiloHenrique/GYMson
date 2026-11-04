package com.example.gymson

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.gymson.data.repository.PerfilRepository
import com.example.gymson.navigation.NavGraph
import com.example.gymson.navigation.Rutas

//Elemento de la barra inferior: ruta, texto e ícono
private data class ItemBarra(val ruta: String, val titulo: String, val icono: ImageVector)

private val itemsBarra = listOf(
    ItemBarra(Rutas.RUTINA, "Rutina", Icons.Default.Home),
    ItemBarra(Rutas.HISTORIAL, "Historial", Icons.AutoMirrored.Filled.List),
    ItemBarra(Rutas.PERFIL, "Perfil", Icons.Default.Person)
)

@Composable
fun EntrenamientoApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route

    //Decidida una sola vez al abrir: sin perfil -> perfil; con perfil -> rutina
    val inicio = remember {
        if (PerfilRepository.perfil.value == null) Rutas.PERFIL else Rutas.RUTINA
    }

    Scaffold(
        bottomBar = {
            //La pantalla "sesión" se abre encima, sin barra inferior
            if (rutaActual != Rutas.SESION) {
                NavigationBar {
                    itemsBarra.forEach { item ->
                        NavigationBarItem(
                            selected = rutaActual == item.ruta,
                            onClick = {
                                navController.navigate(item.ruta) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icono, contentDescription = item.titulo) },
                            label = { Text(item.titulo) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        //innerPadding evita que el contenido quede oculto detrás de la barra
        NavGraph(
            navController = navController,
            startDestination = inicio,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EntrenamientoAppPreview() {
    EntrenamientoApp()
}