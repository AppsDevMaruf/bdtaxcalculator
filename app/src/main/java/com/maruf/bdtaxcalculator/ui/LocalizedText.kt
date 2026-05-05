package com.maruf.bdtaxcalculator.ui

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.Composable

val LocalAppLanguage = compositionLocalOf { AppUiPreferences.languageBangla }

@Composable
fun localizedText(bangla: String, english: String): String {
    return if (LocalAppLanguage.current == AppUiPreferences.languageEnglish) english else bangla
}
