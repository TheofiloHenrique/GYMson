package com.example.gymson.ui.sesion

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymson.R
import com.example.gymson.data.FakeData.rutinaDemo
import com.example.gymson.data.model.EjercicioRutina
import com.example.gymson.data.model.RegistroSerie
import com.example.gymson.ui.components.AnilloCronometro
import com.example.gymson.ui.components.BotonAzulMarino
import com.example.gymson.ui.components.BotonPrincipal
import com.example.gymson.ui.components.BotonSecundario
import com.example.gymson.ui.components.BotonTerciario
import com.example.gymson.ui.components.ImagenEjercicio
import com.example.gymson.ui.components.RegistroSerieForm
import com.example.gymson.ui.components.TarjetaGymson
import com.example.gymson.ui.theme.EntrenamientoPersonalTheme
import com.example.gymson.ui.util.musculoEs

// Versión con ViewModel (la que usa el NavGraph)
@Composable
fun SesionScreen(
    onTerminar: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SesionViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var mostrarSalir by rememberSaveable { mutableStateOf(false) }

    // «Atrás» (y la ✕ del encabezado) piden confirmación mientras hay una sesión en curso
    BackHandler(enabled = uiState.rutina != null) { mostrarSalir = true }

    if (mostrarSalir) {
        AlertDialog(
            onDismissRequest = { mostrarSalir = false },
            title = { Text("¿Salir del entrenamiento?") },
            text = { Text("Si sales ahora, la sesión no se guardará en el historial.") },
            confirmButton = {
                TextButton(onClick = { mostrarSalir = false; onTerminar() }) { Text("Salir") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarSalir = false }) { Text("Continuar") }
            }
        )
    }
    SesionContent(
        uiState = uiState,
        onIniciarDescanso = viewModel::iniciarDescanso,
        onSaltarDescanso = viewModel::saltarDescanso,
        onRegistrar = viewModel::registrarSerie,
        onAvanzar = { viewModel.avanzar(onTerminar) },
        onSalir = { mostrarSalir = true },
        onVolver = onTerminar,
        modifier = modifier
    )
}

// Versión sin ViewModel: solo estado y lambdas -> fácil de previsualizar
@Composable
fun SesionContent(
    uiState: SesionUiState,
    onIniciarDescanso: () -> Unit,
    onSaltarDescanso: () -> Unit,
    onRegistrar: (repeticiones: Int, cargaKg: Float) -> Unit,
    onAvanzar: () -> Unit,
    onSalir: () -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    val actual = uiState.ejercicioRutina
    if (actual == null) {
        Column(
            modifier = modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("No hay una rutina para entrenar.", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))
            BotonSecundario(texto = "Volver", onClick = onVolver)
        }
        return
    }

    val unidad = if (actual.ejercicio.category == "stretching") "s" else "reps"

    // imePadding: cuando aparece el teclado numérico, el contenido sube y el campo no queda tapado
    Column(modifier = modifier.fillMaxSize().imePadding()) {
        // Zona con scroll: las instrucciones pueden ser largas
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            EncabezadoSesion(uiState, onSalir)

            // Al cambiar de ejercicio el cartón anterior sale deslizándose y entra el nuevo
            AnimatedContent(
                targetState = uiState.indiceEjercicio,
                transitionSpec = {
                    (slideInHorizontally(tween(300)) { it / 4 } + fadeIn(tween(300))) togetherWith
                            (slideOutHorizontally(tween(200)) { -it / 4 } + fadeOut(tween(200)))
                },
                label = "ejercicioActual"
            ) { indice ->
                // Se lee con "indice" (no con el ejercicio actual) para que, durante la salida,
                // el cartón viejo siga mostrando su propio ejercicio
                val item = uiState.rutina?.ejercicios?.getOrNull(indice)
                if (item != null) TarjetaEjercicioSesion(item, indice, uiState)
            }

            PanelDescanso(
                uiState = uiState,
                descansoTotal = actual.descansoSegundos,
                onIniciar = onIniciarDescanso,
                onSaltar = onSaltarDescanso
            )

            // key(...): al cambiar de serie o de ejercicio se crea un formulario nuevo
            // y los campos se reinician con los valores de esa serie
            key(uiState.indiceEjercicio, uiState.serieActual) {
                RegistroSerieForm(
                    repsIniciales = actual.repeticiones,
                    cargaInicial = uiState.cargaSugerida,
                    registrada = uiState.serieRegistrada,
                    onRegistrar = onRegistrar,
                    etiquetaReps = if (unidad == "s") "Segundos" else "Repeticiones",
                    numeroSerie = uiState.serieActual,
                    objetivoReps = "Objetivo: ${actual.repeticiones} $unidad"
                )
            }
        }

        BotonAvance(
            uiState = uiState,
            onAvanzar = onAvanzar,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)
        )
    }
}

// ---------- Encabezado y progreso ----------

@Composable
private fun EncabezadoSesion(uiState: SesionUiState, onSalir: () -> Unit) {
    val fraccion = (uiState.indiceEjercicio + 1f) / uiState.totalEjercicios.coerceAtLeast(1)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "SESIÓN EN CURSO",
                style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.sp),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onSalir) {
                Icon(painterResource(R.drawable.ic_close), contentDescription = "Salir del entrenamiento")
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                "Ejercicio ${uiState.indiceEjercicio + 1} de ${uiState.totalEjercicios}",
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                "${(fraccion * 100).toInt()}% completado",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        BarraProgreso(fraccion)
    }
}

// Barra propia (en vez de LinearProgressIndicator) para controlar altura y esquinas;
// el ancho de la parte llena se anima cuando cambia de ejercicio.
@Composable
private fun BarraProgreso(fraccion: Float, modifier: Modifier = Modifier) {
    val animada by animateFloatAsState(
        targetValue = fraccion,
        animationSpec = tween(durationMillis = 500),
        label = "progresoSesion"
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animada)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
        )
    }
}

// ---------- Cartón del ejercicio ----------

@Composable
private fun TarjetaEjercicioSesion(
    item: EjercicioRutina,
    indice: Int,
    uiState: SesionUiState
) {
    val ejercicio = item.ejercicio
    val unidad = if (ejercicio.category == "stretching") "s" else "reps"
    val musculo = ejercicio.primaryMuscles.firstOrNull()?.let(::musculoEs)
    val seriesHechas = uiState.registros
        .filter { it.ejercicioId == ejercicio.id }
        .sortedBy { it.numeroSerie }
    // Las instrucciones empiezan abiertas; si se cierran, se quedan cerradas en ese ejercicio
    var expandidas by rememberSaveable(indice) { mutableStateOf(true) }

    TarjetaGymson(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Box {
                // Las imágenes de la API miden 850×567 (3:2)
                ImagenEjercicio(
                    url = ejercicio.urlImagen(),
                    descripcion = ejercicio.name,
                    modifier = Modifier.fillMaxWidth().aspectRatio(3f / 2f)
                )
                if (musculo != null) {
                    Text(
                        text = musculo,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f), CircleShape)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Text(ejercicio.name, style = MaterialTheme.typography.headlineSmall)

            Text(
                text = "Serie ${uiState.serieActual} de ${item.series} · Objetivo ${item.repeticiones} $unidad",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            uiState.anterior?.let { anterior ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_history),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "  Anterior: ${textoSerie(anterior, unidad)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (seriesHechas.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    seriesHechas.forEach { r ->
                        Text(
                            text = "✓ S${r.numeroSerie} · ${textoSerie(r, unidad)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Instrucciones(
                pasos = ejercicio.instructions,
                expandidas = expandidas,
                onAlternar = { expandidas = !expandidas }
            )
        }
    }
}

@Composable
private fun Instrucciones(
    pasos: List<String>,
    expandidas: Boolean,
    onAlternar: () -> Unit
) {
    val giro by animateFloatAsState(
        targetValue = if (expandidas) 180f else 0f,
        label = "giroFlecha"
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(role = Role.Button, onClick = onAlternar)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_info),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                "Instrucciones de técnica",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(R.drawable.ic_expand_more),
                contentDescription = if (expandidas) "Ocultar instrucciones" else "Mostrar instrucciones",
                modifier = Modifier.graphicsLayer { rotationZ = giro }
            )
        }
        AnimatedVisibility(visible = expandidas) {
            Column(
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Las instrucciones vienen en inglés de la API (se traducen en la fase 7.10)
                pasos.forEachIndexed { i, paso ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "${i + 1}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        Text(
                            paso,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

// ---------- Panel de descanso ----------

private enum class EstadoDescanso { REPOSO, DESCANSANDO, TERMINADO }

@Composable
private fun PanelDescanso(
    uiState: SesionUiState,
    descansoTotal: Int,
    onIniciar: () -> Unit,
    onSaltar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val estado = when {
        uiState.descansando -> EstadoDescanso.DESCANSANDO
        uiState.descansoTerminado -> EstadoDescanso.TERMINADO
        else -> EstadoDescanso.REPOSO
    }
    val siguiente = when {
        !uiState.esUltimaSerie -> "SIGUIENTE: SERIE ${uiState.serieActual + 1}"
        !uiState.esUltimoEjercicio -> "SIGUIENTE: EJERCICIO ${uiState.indiceEjercicio + 2}"
        else -> "ÚLTIMA SERIE"
    }

    // El panel cambia de aspecto con un fundido; el tamaño se anima solo
    AnimatedContent(
        targetState = estado,
        modifier = modifier.fillMaxWidth(),
        transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(150)) },
        label = "panelDescanso"
    ) { e ->
        when (e) {
            EstadoDescanso.REPOSO -> TarjetaGymson(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.ic_timer),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "  Descanso recomendado: ${formatearTiempo(descansoTotal)}",
                            style = MaterialTheme.typography.titleSmall.copy(fontFeatureSettings = "tnum")
                        )
                    }
                    BotonSecundario(
                        texto = "Iniciar descanso",
                        onClick = onIniciar,
                        modifier = Modifier.fillMaxWidth(),
                        icono = R.drawable.ic_timer
                    )
                }
            }

            EstadoDescanso.DESCANSANDO -> TarjetaGymson(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "TIEMPO DE DESCANSO",
                        style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.sp),
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    AnilloCronometro(
                        restanteSegundos = uiState.descansoRestante,
                        totalSegundos = descansoTotal,
                        etiqueta = siguiente
                    )
                    BotonTerciario(
                        texto = "Saltar descanso",
                        onClick = onSaltar,
                        modifier = Modifier.fillMaxWidth(),
                        icono = R.drawable.ic_skip_next
                    )
                }
            }

            EstadoDescanso.TERMINADO -> TarjetaGymson(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CheckAnimado()
                    Text(
                        "¡Listo! Descanso completado",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "Puedes empezar la siguiente serie a tu ritmo.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center
                    )
                    BotonTerciario(
                        texto = "Descansar de nuevo",
                        onClick = onIniciar,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

// El ✓ aparece con un pequeño "rebote": spring con rebote medio, de 0,6 a 1
@Composable
private fun CheckAnimado() {
    val escala = remember { Animatable(0.6f) }
    LaunchedEffect(Unit) {
        escala.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
    }
    Box(
        modifier = Modifier
            .size(56.dp)
            .graphicsLayer { scaleX = escala.value; scaleY = escala.value }
            .background(MaterialTheme.colorScheme.surface, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_check),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        )
    }
}

// ---------- Botón de avance (fijo abajo) ----------

@Composable
private fun BotonAvance(
    uiState: SesionUiState,
    onAvanzar: () -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        !uiState.esUltimaSerie -> BotonPrincipal(
            texto = "Comenzar serie ${uiState.serieActual + 1}",
            onClick = onAvanzar,
            modifier = modifier,
            icono = R.drawable.ic_play_arrow
        )
        !uiState.esUltimoEjercicio -> BotonAzulMarino(
            texto = "Siguiente ejercicio",
            onClick = onAvanzar,
            modifier = modifier,
            icono = R.drawable.ic_arrow_forward
        )
        else -> BotonPrincipal(
            texto = "Terminar sesión",
            onClick = onAvanzar,
            modifier = modifier,
            icono = R.drawable.ic_check
        )
    }
}

// ---------- Utilidades ----------

private fun formatearTiempo(segundos: Int): String =
    "%02d:%02d".format(segundos / 60, segundos % 60)

// 12.0 -> "12"; 22.5 -> "22.5"
private fun formatoKg(valor: Float): String =
    if (valor % 1f == 0f) valor.toInt().toString() else valor.toString()

// "12 kg × 8 reps"; con peso corporal (carga 0): "8 reps (peso corporal)"
private fun textoSerie(r: RegistroSerie, unidad: String): String =
    if (r.cargaKg == 0f) "${r.repeticiones} $unidad (peso corporal)"
    else "${formatoKg(r.cargaKg)} kg × ${r.repeticiones} $unidad"

// ---------- Previews ----------

private val registroDemo = RegistroSerie("Pushups", 1, 6, 0f)

@Preview(showBackground = true)
@Composable
private fun SesionReposoPreview() {
    EntrenamientoPersonalTheme {
        SesionContent(
            uiState = SesionUiState(
                rutina = rutinaDemo,
                registros = listOf(registroDemo),
                ultimoPorEjercicio = mapOf("Pushups" to RegistroSerie("Pushups", 4, 8, 0f))
            ),
            onIniciarDescanso = {}, onSaltarDescanso = {}, onRegistrar = { _, _ -> },
            onAvanzar = {}, onSalir = {}, onVolver = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SesionDescansandoPreview() {
    EntrenamientoPersonalTheme {
        SesionContent(
            uiState = SesionUiState(rutina = rutinaDemo, descansando = true, descansoRestante = 68),
            onIniciarDescanso = {}, onSaltarDescanso = {}, onRegistrar = { _, _ -> },
            onAvanzar = {}, onSalir = {}, onVolver = {}
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SesionTerminadoOscuroPreview() {
    EntrenamientoPersonalTheme {
        SesionContent(
            uiState = SesionUiState(rutina = rutinaDemo, descansoTerminado = true, serieActual = 2),
            onIniciarDescanso = {}, onSaltarDescanso = {}, onRegistrar = { _, _ -> },
            onAvanzar = {}, onSalir = {}, onVolver = {}
        )
    }
}