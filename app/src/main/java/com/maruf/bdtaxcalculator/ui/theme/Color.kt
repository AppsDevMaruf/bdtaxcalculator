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
val TaxDarkBackground = Color(0xFF163300)
val TaxDarkSurface = Color(0xFF203D0D)
val TaxDarkOnBackground = Color(0xFFECF8DD)
val TaxDarkOnSurface = Color(0xFFDCEACB)
val TaxDarkOnPrimary = Color(0xFF173300)
val TaxClassicDarkBackground = Color(0xFF001E16)
val TaxClassicDarkSurface = Color(0xFF08251D)
val TaxClassicDarkOnBackground = Color(0xFFF2FFF7)
val TaxClassicDarkOnSurface = Color(0xFFDDEBDD)
val TaxClassicDarkOnPrimary = Color(0xFF041008)
val TaxBlackDarkBackground = Color(0xFF000000)
val TaxBlackDarkSurface = Color(0xFF101010)
val TaxBlackDarkOnBackground = Color(0xFFFFFFFF)
val TaxBlackDarkOnSurface = Color(0xFFEDEDED)
val TaxBlackDarkOnPrimary = Color(0xFF000000)

@Composable
private fun isDarkThemeActive(): Boolean =
    MaterialTheme.colorScheme.background == TaxDarkBackground ||
        MaterialTheme.colorScheme.background == TaxClassicDarkBackground ||
        MaterialTheme.colorScheme.background == TaxBlackDarkBackground

@Composable
private fun darkColor(olive: Color, classicGreen: Color, midnightBlack: Color): Color {
    return when (LocalAppThemePalette.current) {
        AppThemePalette.ClassicGreen -> classicGreen
        AppThemePalette.MidnightBlack -> midnightBlack
        AppThemePalette.Olive -> olive
    }
}

val HomeActionBlue: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFF9FE870), Color(0xFF7CF59C), Color(0xFFFFFFFF)) else Color(0xFF1F7A39)
val HomeActionBlueDark: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFFECF8DD), Color(0xFFF2FFF7), Color(0xFFFFFFFF)) else Color(0xFF0B4D2B)
val HomeTextPrimary: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFFECF8DD), Color(0xFFEAF8EF), Color(0xFFFFFFFF)) else Color(0xFF1E293B)
val HomeTextMuted: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFFC7D8B5), Color(0xFFA8BFB2), Color(0xFFBDBDBD)) else Color(0xFF64748B)
val HomeBorder: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFF3A5A25), Color(0xFF24493F), Color(0xFF303030)) else Color(0xFFE5E7EB)
val HomeSoftBlue: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFF254511), Color(0xFF123528), Color(0xFF171717)) else Color(0xFFF2F4F8)
val HomeSoftGreen: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFF2D5017), Color(0xFF0E3A27), Color(0xFF202020)) else Color(0xFFF1F8F4)
val HomeSoftPurple: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFF294616), Color(0xFF17322B), Color(0xFF1A1A1A)) else Color(0xFFF5F5FD)
val HomeSoftNav: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFF2D5017), Color(0xFF123528), Color(0xFF202020)) else Color(0xFFEFF3FF)
val HomeNavInactive: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFFA8BA97), Color(0xFF90AA9B), Color(0xFF8E8E8E)) else Color(0xFF94A3B8)

val CalculatorBackground: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(TaxDarkBackground, TaxClassicDarkBackground, TaxBlackDarkBackground) else Color(0xFFF3F7F2)
val CalculatorGradientTop: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFF173800), Color(0xFF002B21), Color(0xFF080808)) else Color(0xFFF5FAF5)
val CalculatorGradientMiddle: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFF163300), Color(0xFF00251C), Color(0xFF050505)) else Color(0xFFEDF5EF)
val CalculatorGradientBottom: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFF0E2400), Color(0xFF00170F), Color(0xFF000000)) else Color(0xFFF9FBF8)
val CalculatorHeroStart: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFF234F09), Color(0xFF004D2D), Color(0xFF181818)) else Color(0xFF103A22)
val CalculatorHeroMiddle: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFF3D7D18), Color(0xFF087A43), Color(0xFF2A2A2A)) else Color(0xFF1F6F3B)
val CalculatorInk: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFFECF8DD), Color(0xFFF2FFF7), Color(0xFFFFFFFF)) else Color(0xFF102016)
val CalculatorControlText: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFFDCEACB), Color(0xFFD4E8DC), Color(0xFFEDEDED)) else Color(0xFF244033)
val CalculatorFieldText: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFFDCEACB), Color(0xFFD4E8DC), Color(0xFFEDEDED)) else Color(0xFF4A554F)
val CalculatorMuted: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFFC7D8B5), Color(0xFFA8BFB2), Color(0xFFBDBDBD)) else Color(0xFF6B756E)
val CalculatorMutedSoft: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFFA8BA97), Color(0xFF85A093), Color(0xFF8E8E8E)) else Color(0xFF79867F)
val CalculatorSurfaceAlt: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFF203D0D), Color(0xFF0D2B23), Color(0xFF151515)) else Color(0xFFF8FAF8)
val CalculatorPanel: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFF1B3708), Color(0xFF08281F), Color(0xFF111111)) else Color(0xFFFBFDFA)
val CalculatorBorder: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFF3A5A25), Color(0xFF24493F), Color(0xFF2E2E2A)) else Color(0xFFE4E9E5)
val CalculatorDivider: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFF2D4C19), Color(0xFF17392F), Color(0xFF242424)) else Color(0xFFF1F3F1)
val CalculatorAccentSoft: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFF254511), Color(0xFF102F25), Color(0xFF1A1A18)) else Color(0xFFF2F7F3)
val CalculatorHeroPill: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFF314F1B), Color(0xFF163D2E), Color(0xFF2A2A2A)) else Color(0xFFD4E7D8)
val CalculatorSuccess: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFF9FE870), Color(0xFF72F49A), Color(0xFFFFFFFF)) else Color(0xFF1F7A39)
val CalculatorPositive: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFFC4F5A6), Color(0xFF8CF0A3), Color(0xFFEDEDED)) else Color(0xFF81C784)
val CalculatorDanger: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFFFF6B6B), Color(0xFFFF5A5F), Color(0xFFFF6B6B)) else Color(0xFFD32F2F)
val CalculatorDangerSoft: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFFFF9A91), Color(0xFFFF8B8E), Color(0xFFFF9A91)) else Color(0xFFE57373)
val CalculatorDangerSoft2: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFF4A251C), Color(0xFF3B2020), Color(0xFF321A1A)) else Color(0x33EC7B7B)
val CalculatorInfoBackground: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFF2E4C1B), Color(0xFF123D2C), Color(0xFF202020)) else Color(0xFFE3F2FD)
val CalculatorInfo: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFFC4F5A6), Color(0xFF8CF0A3), Color(0xFFEDEDED)) else Color(0xFF1565C0)
val CalculatorInfoDark: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFFD9F8BE), Color(0xFF72F49A), Color(0xFFFFFFFF)) else Color(0xFF0D47A1)

val AuditDanger: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFFFF6B6B), Color(0xFFFF5A5F), Color(0xFFFF6B6B)) else Color(0xFFCF1322)
val AuditScreenBackground: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(TaxDarkBackground, TaxClassicDarkBackground, TaxBlackDarkBackground) else Color(0xFFF4FBF6)
val AuditInputBackground: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFF203D0D), Color(0xFF0D2B23), Color(0xFF151515)) else Color(0xFFFAFDFC)
val AuditDisabledButton: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFF2D4C19), Color(0xFF17392F), Color(0xFF242424)) else Color(0xFFE2E8F0)
val AuditDisabledText: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFFA8BA97), Color(0xFF85A093), Color(0xFF8C8C84)) else Color(0xFF8FA1B5)
val AuditReadyPill: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFF2D5017), Color(0xFF123D2C), Color(0xFF202020)) else Color(0xFFE7F8ED)
val AuditReadyText: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFF9FE870), Color(0xFF72F49A), Color(0xFFFFFFFF)) else Color(0xFF2E8A57)
val AuditSelectedPill: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFF4A251C), Color(0xFF3B2020), Color(0xFF321A1A)) else Color(0xFFFFEFEF)
val AuditSelectedText: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFFFF9A91), Color(0xFFFF8B8E), Color(0xFFFF9A91)) else Color(0xFFD94A4A)
val AuditZonePill: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFF2E4C1B), Color(0xFF123D2C), Color(0xFF202020)) else Color(0xFFEFF4FF)
val AuditZoneText: Color
    @Composable get() = if (isDarkThemeActive()) darkColor(Color(0xFFC4F5A6), Color(0xFF8CF0A3), Color(0xFFEDEDED)) else Color(0xFF3F6FE5)
