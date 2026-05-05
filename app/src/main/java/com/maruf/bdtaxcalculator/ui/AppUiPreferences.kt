package com.maruf.bdtaxcalculator.ui

import android.content.Context
import androidx.core.content.edit

object AppUiPreferences {
    private const val preferencesName = "taxpro_ui_preferences"
    private const val keyLanguage = "language"
    private const val keyThemeMode = "theme_mode"

    const val languageBangla = "bn"
    const val languageEnglish = "en"

    const val themeSystem = "system"
    const val themeLight = "light"
    const val themeDark = "dark"

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
        return preferences(context).getString(keyThemeMode, themeSystem)
            ?.takeIf { it == themeSystem || it == themeLight || it == themeDark }
            ?: themeSystem
    }

    fun setThemeMode(context: Context, themeMode: String) {
        if (themeMode != themeSystem && themeMode != themeLight && themeMode != themeDark) return
        preferences(context).edit { putString(keyThemeMode, themeMode) }
    }

    private fun preferences(context: Context) =
        context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
}
