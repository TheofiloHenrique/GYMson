package com.example.gymson.data.repository

import com.google.android.gms.tasks.Task
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

// Traduce inglés -> español en el dispositivo (ML Kit) y guarda los resultados en memoria.
// Si algo falla (sin red para bajar el modelo, etc.) devuelve menos textos y la UI muestra el original.
object TraductorRepository {

    // Casos donde el traductor automático suele fallar: se revisan primero
    private val excepciones = mapOf(
        "Pushups" to "Flexiones de brazos",
        "Plank" to "Plancha",
        "Bodyweight Squat" to "Sentadilla con peso corporal"
    )

    private val cache = ConcurrentHashMap<String, String>()
    private val mutex = Mutex()          // evita bajar el modelo dos veces a la vez
    private var modeloListo = false

    // "by lazy": el cliente se crea solo cuando se necesita
    private val traductor by lazy {
        Translation.getClient(
            TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.SPANISH)
                .build()
        )
    }

    /** Devuelve el mapa «texto original -> traducción» solo con los que se lograron traducir. */
    suspend fun traducirTextos(textos: List<String>): Map<String, String> {
        excepciones.forEach { (en, es) -> cache.putIfAbsent(en, es) }

        val pendientes = textos.distinct().filter { !cache.containsKey(it) }
        if (pendientes.isNotEmpty() && asegurarModelo()) {
            for (texto in pendientes) {
                try {
                    cache[texto] = traductor.translate(texto).esperar()
                } catch (e: CancellationException) {
                    throw e                      // no tragarse la cancelación de la corrutina
                } catch (e: Exception) {
                    // ese texto se queda en inglés
                }
            }
        }
        return textos.mapNotNull { t -> cache[t]?.let { t to it } }.toMap()
    }

    // Descarga el modelo (~30 MB) la primera vez; después ya está en el teléfono
    private suspend fun asegurarModelo(): Boolean = mutex.withLock {
        if (modeloListo) return@withLock true
        try {
            traductor.downloadModelIfNeeded(DownloadConditions.Builder().build()).esperar()
            modeloListo = true
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            modeloListo = false
        }
        modeloListo
    }
}

// Convierte una Task de Google (callbacks) en una función suspend
private suspend fun <T> Task<T>.esperar(): T = suspendCancellableCoroutine { cont ->
    addOnSuccessListener { cont.resume(it) }
    addOnFailureListener { cont.resumeWithException(it) }
    addOnCanceledListener { cont.cancel() }
}