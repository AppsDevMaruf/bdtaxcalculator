package com.maruf.bdtaxcalculator.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.isSystemInDarkTheme

private val LightColorScheme = lightColorScheme(
    primary = TaxPrimary,
    secondary = TaxSecondary,
    tertiary = TaxTertiary,
    background = TaxBackground,
    surface = TaxSurface,
    onPrimary = TaxSurface,
    onSecondary = TaxSurface,
    onBackground = TaxPrimaryDark,
    onSurface = TaxPrimaryDark
)

private val DarkColorScheme = darkColorScheme(
    primary = TaxTertiary,
    secondary = TaxSecondary,
    tertiary = TaxPrimary,
    background = TaxDarkBackground,
    surface = TaxDarkSurface,
    onPrimary = TaxDarkOnPrimary,
    onSecondary = TaxDarkOnPrimary,
    onBackground = TaxDarkOnBackground,
    onSurface = TaxDarkOnSurface
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
