package com.maruf.bdtaxcalculator.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf

enum class AppThemePalette(val id: String) {
    Olive("olive"),
    ClassicGreen("classic_green"),
    MidnightBlack("midnight_black");

    companion object {
        fun fromId(id: String): AppThemePalette {
            return entries.firstOrNull { it.id == id } ?: ClassicGreen
        }
    }
}

val LocalAppThemePalette = staticCompositionLocalOf { AppThemePalette.ClassicGreen }
