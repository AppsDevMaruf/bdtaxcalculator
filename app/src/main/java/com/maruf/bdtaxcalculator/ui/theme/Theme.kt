package com.maruf.bdtaxcalculator.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

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

@Composable
fun BDTaxCalculatorTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
