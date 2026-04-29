package com.maruf.bdtaxcalculator.tax

import java.text.NumberFormat
import java.util.Locale

fun formatBengaliNumber(number: Long): String {
    val bengaliDigits = arrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
    val formatted = NumberFormat.getNumberInstance(Locale.US).format(number)
    return formatted.map { char ->
        if (char.isDigit()) bengaliDigits[char.toString().toInt()] else char
    }.joinToString("")
}

fun formatEnglishNumber(number: Long): String =
    NumberFormat.getNumberInstance(Locale.US).format(number)
