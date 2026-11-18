package com.example.gymson.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.gymson.R

val NunitoSans = FontFamily(
    Font(R.font.nunito_sans_medium, FontWeight.Medium),
    Font(R.font.nunito_sans_semibold, FontWeight.SemiBold),
    Font(R.font.nunito_sans_bold, FontWeight.Bold),
    Font(R.font.nunito_sans_extrabold, FontWeight.ExtraBold)
)

// "tnum" = números tabulares: el cronómetro no "baila" al contar
private fun estilo(peso: FontWeight, tamanho: Int, altura: Int, tabular: Boolean = false) =
    TextStyle(
        fontFamily = NunitoSans,
        fontWeight = peso,
        fontSize = tamanho.sp,
        lineHeight = altura.sp,
        fontFeatureSettings = if (tabular) "tnum" else null
    )

// Una sola escala para claro y oscuro
val Typography = Typography(
    displayLarge = estilo(FontWeight.ExtraBold, 64, 72, tabular = true),
    displayMedium = estilo(FontWeight.ExtraBold, 48, 56, tabular = true), // cronômetro
    headlineLarge = estilo(FontWeight.ExtraBold, 32, 40),
    headlineMedium = estilo(FontWeight.ExtraBold, 26, 32),
    headlineSmall = estilo(FontWeight.Bold, 22, 28),
    titleLarge = estilo(FontWeight.Bold, 18, 24),
    titleMedium = estilo(FontWeight.Bold, 16, 24),
    titleSmall = estilo(FontWeight.Bold, 14, 20),
    bodyLarge = estilo(FontWeight.Medium, 16, 24),
    bodyMedium = estilo(FontWeight.Medium, 14, 20),
    bodySmall = estilo(FontWeight.Medium, 12, 16),
    labelLarge = estilo(FontWeight.Bold, 15, 20),
    labelMedium = estilo(FontWeight.SemiBold, 13, 18),
    labelSmall = estilo(FontWeight.SemiBold, 11, 16)
)