package com.example.gymson

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.gymson.data.repository.PerfilRepository
import com.example.gymson.navigation.NavGraph
import com.example.gymson.navigation.Rutas
import com.example.gymson.ui.components.EncabezadoGymson

//Elemento de la barra inferior: ruta, texto e ícono
private data class ItemBarra(
    val ruta: String,
    val titulo: String,
    @DrawableRes val icono: Int,
    @DrawableRes val iconoActivo: Int
)

private val itemsBarra = listOf(
    ItemBarra(Rutas.RUTINA, "Rutina", R.drawable.ic_fitness_center, R.drawable.ic_fitness_center_fill),
    ItemBarra(Rutas.HISTORIAL, "Historial", R.drawable.ic_history, R.drawable.ic_history_fill),
    ItemBarra(Rutas.PERFIL, "Perfil", R.drawable.ic_person, R.drawable.ic_person_fill)
)

private fun tituloDe(ruta: String?) = when (ruta) {
    Rutas.RUTINA -> "Rutina del día"
    Rutas.HISTORIAL -> "Historial"
    Rutas.PERFIL -> "Perfil"
    else -> ""
}

@Composable
fun EntrenamientoApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route

    //Decidida una sola vez al abrir: sin perfil -> perfil; con perfil -> rutina
    val inicio = rememberSaveable {
        if (PerfilRepository.perfil.value == null) Rutas.PERFIL else Rutas.RUTINA
    }

    Scaffold(

        topBar = {
            if (rutaActual != Rutas.SESION) EncabezadoGymson(tituloDe(rutaActual))
        },

        bottomBar = {
            if (rutaActual != Rutas.SESION) {
                Column {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        tonalElevation = 0.dp
                    ) {
                        itemsBarra.forEach { item ->
                            val activo = rutaActual == item.ruta
                            NavigationBarItem(
                                selected = activo,
                                onClick = {
                                    navController.navigate(item.ruta) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        painterResource(if (activo) item.iconoActivo else item.icono),
                                        contentDescription = item.titulo
                                    )
                                },
                                label = { Text(item.titulo) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
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
