package com.maruf.bdtaxcalculator.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val TaxPrimary = Color(0xFF2E7D32)
val TaxPrimaryDark = Color(0xFF1B5E20)
val TaxSecondary = Color(0xFF00897B)
val TaxTertiary = Color(0xFF4CAF50)
val TaxBackground = Color(0xFFF1F8E9)
val TaxSurface = Color(0xFFFFFFFF)
val TaxDarkBackground = Color(0xFF001E16)
val TaxDarkSurface = Color(0xFF08251D)
val TaxDarkOnBackground = Color(0xFFF2FFF7)
val TaxDarkOnSurface = Color(0xFFD8E9DE)
val TaxDarkOnPrimary = Color(0xFF002013)

@Composable
private fun isDarkThemeActive(): Boolean =
    MaterialTheme.colorScheme.background == TaxDarkBackground

val HomeActionBlue: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFF7CF59C) else Color(0xFF1F7A39)
val HomeActionBlueDark: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFFF2FFF7) else Color(0xFF0B4D2B)
val HomeTextPrimary: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFFEAF8EF) else Color(0xFF1E293B)
val HomeTextMuted: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFFA8BFB2) else Color(0xFF64748B)
val HomeBorder: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFF24493F) else Color(0xFFE5E7EB)
val HomeSoftBlue: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFF123528) else Color(0xFFF2F4F8)
val HomeSoftGreen: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFF0E3A27) else Color(0xFFF1F8F4)
val HomeSoftPurple: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFF17322B) else Color(0xFFF5F5FD)
val HomeSoftNav: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFF123528) else Color(0xFFEFF3FF)
val HomeNavInactive: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFF90AA9B) else Color(0xFF94A3B8)

val CalculatorBackground: Color
    @Composable get() = if (isDarkThemeActive()) TaxDarkBackground else Color(0xFFF3F7F2)
val CalculatorGradientTop: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFF002B21) else Color(0xFFF5FAF5)
val CalculatorGradientMiddle: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFF00251C) else Color(0xFFEDF5EF)
val CalculatorGradientBottom: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFF00170F) else Color(0xFFF9FBF8)
val CalculatorHeroStart: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFF004D2D) else Color(0xFF103A22)
val CalculatorHeroMiddle: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFF087A43) else Color(0xFF1F6F3B)
val CalculatorInk: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFFF2FFF7) else Color(0xFF102016)
val CalculatorControlText: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFFD4E8DC) else Color(0xFF244033)
val CalculatorFieldText: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFFD4E8DC) else Color(0xFF4A554F)
val CalculatorMuted: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFFA8BFB2) else Color(0xFF6B756E)
val CalculatorMutedSoft: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFF85A093) else Color(0xFF79867F)
val CalculatorSurfaceAlt: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFF0D2B23) else Color(0xFFF8FAF8)
val CalculatorPanel: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFF08281F) else Color(0xFFFBFDFA)
val CalculatorBorder: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFF24493F) else Color(0xFFE4E9E5)
val CalculatorDivider: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFF17392F) else Color(0xFFF1F3F1)
val CalculatorAccentSoft: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFF102F25) else Color(0xFFF2F7F3)
val CalculatorHeroPill: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFF163D2E) else Color(0xFFD4E7D8)
val CalculatorSuccess: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFF72F49A) else Color(0xFF1F7A39)
val CalculatorPositive: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFF8CF0A3) else Color(0xFF81C784)
val CalculatorDanger: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFFFF5A5F) else Color(0xFFD32F2F)
val CalculatorDangerSoft: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFFFF8B8E) else Color(0xFFE57373)
val CalculatorDangerSoft2: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFF3B2020) else Color(0x33EC7B7B)
val CalculatorInfoBackground: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFF102E45) else Color(0xFFE3F2FD)
val CalculatorInfo: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFF8FB8FF) else Color(0xFF1565C0)
val CalculatorInfoDark: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFF72F49A) else Color(0xFF0D47A1)

val AuditDanger: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFFFF5A5F) else Color(0xFFCF1322)
val AuditScreenBackground: Color
    @Composable get() = if (isDarkThemeActive()) TaxDarkBackground else Color(0xFFF4FBF6)
val AuditInputBackground: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFF0D2B23) else Color(0xFFFAFDFC)
val AuditDisabledButton: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFF17392F) else Color(0xFFE2E8F0)
val AuditDisabledText: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFF85A093) else Color(0xFF8FA1B5)
val AuditReadyPill: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFF123D2C) else Color(0xFFE7F8ED)
val AuditReadyText: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFF72F49A) else Color(0xFF2E8A57)
val AuditSelectedPill: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFF3B2020) else Color(0xFFFFEFEF)
val AuditSelectedText: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFFFF8B8E) else Color(0xFFD94A4A)
val AuditZonePill: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFF102E45) else Color(0xFFEFF4FF)
val AuditZoneText: Color
    @Composable get() = if (isDarkThemeActive()) Color(0xFF8FB8FF) else Color(0xFF3F6FE5)
