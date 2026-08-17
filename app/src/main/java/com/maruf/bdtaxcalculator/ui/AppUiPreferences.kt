package com.maruf.bdtaxcalculator.ui

import android.content.Context
import androidx.core.content.edit
import com.maruf.bdtaxcalculator.ui.theme.AppThemePalette

object AppUiPreferences {
    private const val preferencesName = "taxpro_ui_preferences"
    private const val keyLanguage = "language"
    private const val keyThemeMode = "theme_mode"
    private const val keyThemePalette = "theme_palette"
    private const val keyOnboardingCompleted = "onboarding_completed"

    const val languageBangla = "bn"
    const val languageEnglish = "en"

    const val themeSystem = "system"
    const val themeLight = "light"
    const val themeDark = "dark"

    const val themePaletteOlive = "olive"
    const val themePaletteClassicGreen = "classic_green"
    const val themePaletteMidnightBlack = "midnight_black"
    const val defaultThemeMode = themeLight
    const val defaultThemePalette = themePaletteClassicGreen

    fun getLanguage(context: Context): String {
        return preferences(context).getString(keyLanguage, languageBangla)
            ?.takeIf { it == languageBangla || it == languageEnglish }
            ?: languageBangla
    }

    fun setLanguage(context: Context, language: String) {
        if (language != languageBangla && language != languageEnglish) return
        preferences(context).edit { putString(keyLanguage, language) }
    }

    fun getThemeMode(context: Context): String {
        return when (preferences(context).getString(keyThemeMode, defaultThemeMode)) {
            themeDark -> themeDark
            themeLight -> themeLight
            else -> defaultThemeMode
        }
    }

    fun setThemeMode(context: Context, themeMode: String) {
        if (themeMode != themeLight && themeMode != themeDark) return
        preferences(context).edit { putString(keyThemeMode, themeMode) }
    }

    fun getThemePalette(context: Context): String {
        return preferences(context).getString(keyThemePalette, defaultThemePalette)
            ?.takeIf {
                it == AppThemePalette.Olive.id ||
                    it == AppThemePalette.ClassicGreen.id ||
                    it == AppThemePalette.MidnightBlack.id
            }
            ?: defaultThemePalette
    }

    fun setThemePalette(context: Context, themePalette: String) {
        if (themePalette != AppThemePalette.Olive.id &&
            themePalette != AppThemePalette.ClassicGreen.id &&
            themePalette != AppThemePalette.MidnightBlack.id
        ) {
            return
        }
        preferences(context).edit { putString(keyThemePalette, themePalette) }
    }

    fun hasCompletedOnboarding(context: Context): Boolean {
        return preferences(context).getBoolean(keyOnboardingCompleted, false)
    }

    fun setOnboardingCompleted(context: Context) {
        preferences(context).edit { putBoolean(keyOnboardingCompleted, true) }
    }

    private fun preferences(context: Context) =
        context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
}
