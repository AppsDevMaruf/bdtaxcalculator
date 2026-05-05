package com.maruf.bdtaxcalculator.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = TaxPrimary,
    secondary = TaxSecondary,
    tertiary = TaxTertiary,
    background = TaxBackground,
    surface = TaxSurface,
    surfaceVariant = Color(0xFFF3F7F2),
    outline = Color(0xFFE4E9E5),
    onPrimary = TaxSurface,
    onSecondary = TaxSurface,
    onBackground = TaxPrimaryDark,
    onSurface = TaxPrimaryDark,
    onSurfaceVariant = Color(0xFF4A554F),
    error = Color(0xFFD32F2F)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF72F49A),
    secondary = Color(0xFF4DD0B2),
    tertiary = Color(0xFFB7F7C8),
    background = TaxDarkBackground,
    surface = TaxDarkSurface,
    surfaceVariant = Color(0xFF0D2B23),
    outline = Color(0xFF24493F),
    onPrimary = TaxDarkOnPrimary,
    onSecondary = TaxDarkOnPrimary,
    onBackground = TaxDarkOnBackground,
    onSurface = TaxDarkOnSurface,
    onSurfaceVariant = Color(0xFFD4E8DC),
    error = Color(0xFFFF5A5F)
)

@Composable
fun BDTaxCalculatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = AppTypography,
        content = content
    )
}
