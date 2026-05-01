package com.maruf.bdtaxcalculator.tax

import java.text.NumberFormat
import java.util.Locale

fun formatBengaliNumber(number: Long): String =
    NumberFormat.getNumberInstance(Locale.US).format(number).toBengaliDigits()

fun formatBengaliPercent(value: Double): String {
    return "%.2f"
        .format(Locale.US, value)
        .trimEnd('0')
        .trimEnd('.')
        .toBengaliDigits() + "%"
}

private fun String.toBengaliDigits(): String {
    val bengaliDigits = charArrayOf(
        '\u09E6',
        '\u09E7',
        '\u09E8',
        '\u09E9',
        '\u09EA',
        '\u09EB',
        '\u09EC',
        '\u09ED',
        '\u09EE',
        '\u09EF'
    )
    return map { char ->
        if (char.isDigit()) bengaliDigits[char.toString().toInt()] else char
    }.joinToString("")
}

fun formatEnglishNumber(number: Long): String =
    NumberFormat.getNumberInstance(Locale.US).format(number)
