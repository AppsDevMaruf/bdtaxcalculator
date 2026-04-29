package com.maruf.bdtaxcalculator.audit

data class AuditRecord(
    val zoneIndex: Int,
    val circleIndex: Int,
    val category: String,
    val assessmentYear: String,
    val isSelected: Boolean
)

data class AuditLookupResult(
    val tin: String,
    val zone: String,
    val circle: String,
    val category: String,
    val assessmentYear: String,
    val isSelected: Boolean
)

data class AuditDataset(
    val zones: List<String>,
    val circles: List<String>,
    val records: Map<String, AuditRecord>
) {
    val totalRecords: Int = records.size
    val selectedCount: Int = records.count { it.value.isSelected }
    val notSelectedCount: Int = totalRecords - selectedCount
    val zoneCount: Int = zones.size

    fun lookupTin(rawTin: String): AuditLookupResult? {
        val normalizedTin = normalizeTin(rawTin)
        val record = records[normalizedTin] ?: return null

        return AuditLookupResult(
            tin = normalizedTin,
            zone = zones.getOrElse(record.zoneIndex) { "Unknown Zone" },
            circle = circles.getOrElse(record.circleIndex) { "Unknown Circle" },
            category = record.category,
            assessmentYear = record.assessmentYear,
            isSelected = record.isSelected
        )
    }
}

fun normalizeTin(input: String): String = input.filter(Char::isDigit)

fun maskTin(tin: String): String {
    val normalizedTin = normalizeTin(tin)
    if (normalizedTin.length <= 4) return normalizedTin
    val hiddenCount = normalizedTin.length - 4
    return buildString {
        append(normalizedTin.take(2))
        repeat(hiddenCount) { append('•') }
        append(normalizedTin.takeLast(2))
    }
}
