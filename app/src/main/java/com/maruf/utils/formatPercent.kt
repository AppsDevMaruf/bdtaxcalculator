package com.maruf.utils

fun Double.formatPercent(): String {
    return "%.2f".format(this).trimEnd('0').trimEnd('.') + "%"
}