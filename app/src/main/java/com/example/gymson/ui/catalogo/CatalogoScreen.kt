package com.example.gymson.ui.catalogo

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymson.data.FakeData.ejerciciosDemo
import com.example.gymson.ui.components.EstadoCargando
import com.example.gymson.ui.components.EstadoError
import com.example.gymson.ui.components.TarjetaEjercicio
import com.example.gymson.ui.theme.EntrenamientoPersonalTheme

// Versión con ViewModel (la que usa el NavGraph)
@Composable
fun CatalogoScreen(
    modifier: Modifier = Modifier,
    viewModel: CatalogoViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    CatalogoContent(
        uiState = uiState,
        onReintentar = viewModel::cargar,
        modifier = modifier
    )
}

// Versión sin ViewModel: solo recibe estado y lambdas -> fácil de previsualizar
@Composable
fun CatalogoContent(
    uiState: CatalogoUiState,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        is CatalogoUiState.Cargando -> EstadoCargando(modifier)
        is CatalogoUiState.Error -> EstadoError(uiState.mensaje, onReintentar, modifier)
        is CatalogoUiState.Exito -> LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // LazyColumn solo compone los elementos visibles (hay ~870)
            items(uiState.ejercicios, key = { it.id }) { ejercicio ->
                TarjetaEjercicio(ejercicio)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CatalogoExitoPreview() {
    EntrenamientoPersonalTheme {
        CatalogoContent(CatalogoUiState.Exito(ejerciciosDemo), onReintentar = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun CatalogoErrorPreview() {
    EntrenamientoPersonalTheme {
        CatalogoContent(CatalogoUiState.Error("Sin conexión"), onReintentar = {})
    }
}
