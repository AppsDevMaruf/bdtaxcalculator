package com.maruf.bdtaxcalculator.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.maruf.bdtaxcalculator.R
import com.maruf.bdtaxcalculator.ui.AppUiPreferences
import com.maruf.bdtaxcalculator.ui.LocalAppLanguage

private val BanglaFontFamily = FontFamily(
    Font(R.font.tiro_bangla_regular, FontWeight.Normal),
    Font(R.font.tiro_bangla_italic, FontWeight.Normal)
)

private val QuicksandFontFamily = FontFamily(
    Font(R.font.quicksand_light, FontWeight.Light),
    Font(R.font.quicksand_regular, FontWeight.Normal),
    Font(R.font.quicksand_medium, FontWeight.Medium),
    Font(R.font.quicksand_semibold, FontWeight.SemiBold),
    Font(R.font.quicksand_bold, FontWeight.Bold)
)

val TiroBanglaFontFamily: FontFamily
    @Composable get() = if (LocalAppLanguage.current == AppUiPreferences.languageEnglish) {
        QuicksandFontFamily
    } else {
        BanglaFontFamily
    }

private val BanglaTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = BanglaFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)

private val EnglishTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = QuicksandFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)

val AppTypography: Typography
    @Composable get() = if (LocalAppLanguage.current == AppUiPreferences.languageEnglish) {
        EnglishTypography
    } else {
        BanglaTypography
    }
