package com.maruf.bdtaxcalculator.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.CompositionLocalProvider
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
    primary = Color(0xFF9FE870),
    secondary = Color(0xFFC4F5A6),
    tertiary = Color(0xFFECF8DD),
    background = TaxDarkBackground,
    surface = TaxDarkSurface,
    surfaceVariant = Color(0xFF203D0D),
    outline = Color(0xFF3A5A25),
    onPrimary = TaxDarkOnPrimary,
    onSecondary = TaxDarkOnPrimary,
    onBackground = TaxDarkOnBackground,
    onSurface = TaxDarkOnSurface,
    onSurfaceVariant = Color(0xFFDCEACB),
    error = Color(0xFFFF6B6B)
)

private val ClassicGreenDarkColorScheme = darkColorScheme(
    primary = Color(0xFF72F49A),
    secondary = Color(0xFF8CF0A3),
    tertiary = Color(0xFFF2FFF7),
    background = TaxClassicDarkBackground,
    surface = TaxClassicDarkSurface,
    surfaceVariant = Color(0xFF0D2B23),
    outline = Color(0xFF24493F),
    onPrimary = TaxClassicDarkOnPrimary,
    onSecondary = TaxClassicDarkOnPrimary,
    onBackground = TaxClassicDarkOnBackground,
    onSurface = TaxClassicDarkOnSurface,
    onSurfaceVariant = Color(0xFFD4E8DC),
    error = Color(0xFFFF5A5F)
)

private val MidnightBlackDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFFFFF),
    secondary = Color(0xFFEDEDED),
    tertiary = Color(0xFFBDBDBD),
    background = TaxBlackDarkBackground,
    surface = TaxBlackDarkSurface,
    surfaceVariant = Color(0xFF151515),
    outline = Color(0xFF303030),
    onPrimary = TaxBlackDarkOnPrimary,
    onSecondary = TaxBlackDarkOnPrimary,
    onBackground = TaxBlackDarkOnBackground,
    onSurface = TaxBlackDarkOnSurface,
    onSurfaceVariant = Color(0xFFEDEDED),
    error = Color(0xFFFF6B6B)
)

@Composable
fun BDTaxCalculatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themePalette: String = AppThemePalette.Olive.id,
    content: @Composable () -> Unit
) {
    val appThemePalette = AppThemePalette.fromId(themePalette)
    val colorScheme = when {
        darkTheme && appThemePalette == AppThemePalette.MidnightBlack -> MidnightBlackDarkColorScheme
        darkTheme && appThemePalette == AppThemePalette.ClassicGreen -> ClassicGreenDarkColorScheme
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    CompositionLocalProvider(LocalAppThemePalette provides appThemePalette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}
