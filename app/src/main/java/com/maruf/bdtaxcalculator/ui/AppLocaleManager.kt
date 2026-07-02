package com.maruf.bdtaxcalculator.ui

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object AppLocaleManager {
    fun apply(language: String) {
        val languageTag = language.toLanguageTag()
        val currentTag = AppCompatDelegate.getApplicationLocales().toLanguageTags()
        if (currentTag == languageTag) return

        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(languageTag)
        )
    }

    private fun String.toLanguageTag(): String {
        return when (this) {
            AppUiPreferences.languageEnglish -> "en"
            else -> "bn"
        }
    }
}
