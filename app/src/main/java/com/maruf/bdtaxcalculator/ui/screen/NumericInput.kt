package com.maruf.bdtaxcalculator.ui.screen

internal const val MaxMoneyInputLength = 15
internal const val TinInputLength = 12

internal fun normalizeAuditTinInput(input: String): String {
    return buildString {
        input.forEach { char ->
            when (char) {
                in '0'..'9' -> append(char)
                in '০'..'৯' -> append(('0'.code + char.code - '০'.code).toChar())
                in '٠'..'٩' -> append(('0'.code + char.code - '٠'.code).toChar())
            }
        }
    }.take(TinInputLength)
}
