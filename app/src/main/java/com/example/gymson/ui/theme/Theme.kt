package com.example.gymson.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val ColoresClaros = lightColorScheme(
    primary = VerdeMarca, onPrimary = Color.White,
    primaryContainer = Menta, onPrimaryContainer = Color(0xFF00201C),
    secondary = AzulMarino, onSecondary = Color.White,
    secondaryContainer = Color(0xFFDCE6F2), onSecondaryContainer = AzulMarino,
    tertiary = Coral, onTertiary = TextoOscuro, // acento: só ação principal e cronômetro
    tertiaryContainer = Color(0xFFFFE1D4), onTertiaryContainer = TextoOscuro,
    background = Color(0xFFF6F4EF), onBackground = TextoOscuro,
    surface = Color(0xFFF6F4EF), onSurface = TextoOscuro,
    surfaceVariant = Color(0xFFEFEBE3), onSurfaceVariant = Color(0xFF4A5A5E),
    surfaceContainerLowest = Color.White, surfaceContainerLow = Color.White, // cartões
    surfaceContainer = Color(0xFFF0EEE9), surfaceContainerHigh = Color(0xFFEAE8E3),
    surfaceContainerHighest = Color(0xFFE4E2DD),
    outline = Color(0xFF6E7977), outlineVariant = Color(0xFFE5E0D8),
    error = Color(0xFFBA1A1A), onError = Color.White,
    errorContainer = Color(0xFFFFDAD6), onErrorContainer = Color(0xFF93000A)
)

private val ColoresOscuros = darkColorScheme(
    primary = Color(0xFF66D9CB), onPrimary = Color(0xFF003732),
    primaryContainer = VerdeMarca, onPrimaryContainer = Color.White,
    secondary = Color(0xFF9FC4F0), onSecondary = Color(0xFF0F2A47),
    secondaryContainer = Color(0xFF1D3B5E), onSecondaryContainer = Color(0xFFD6E6FA),
    tertiary = Coral, onTertiary = TextoOscuro,
    tertiaryContainer = Color(0xFF5A2A14), onTertiaryContainer = Color(0xFFFFD5C7),
    background = Color(0xFF0F1A1C), onBackground = Color(0xFFE3EBE8),
    surface = Color(0xFF0F1A1C), onSurface = Color(0xFFE3EBE8),
    surfaceVariant = Color(0xFF1D2E32), onSurfaceVariant = Color(0xFF9FB5B0),
    surfaceContainerLowest = Color(0xFF121D20), surfaceContainerLow = Color(0xFF162427), // cartões
    surfaceContainer = Color(0xFF1D2E32), surfaceContainerHigh = Color(0xFF24393E),
    surfaceContainerHighest = Color(0xFF2C3F44),
    outline = Color(0xFF869390), outlineVariant = Color(0xFF2B4247),
    error = Color(0xFFFFB4AB), onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A), onErrorContainer = Color(0xFFFFDAD6)
)

private val Formas = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun EntrenamientoPersonalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    //Colores dinámicos (Material You) solo existen en Android 12+ (API 31)
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> ColoresOscuros
        else -> ColoresClaros
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
        shapes = Formas
    )
}