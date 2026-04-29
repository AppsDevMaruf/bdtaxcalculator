package com.maruf.bdtaxcalculator.audit

import android.content.Context
import org.json.JSONObject

object AuditRepository {
    private const val assetFileName = "audit.json"

    @Volatile
    private var cachedDataset: AuditDataset? = null

    fun load(context: Context): AuditDataset {
        cachedDataset?.let { return it }

        synchronized(this) {
            cachedDataset?.let { return it }

            val rawJson = context.assets.open(assetFileName).bufferedReader().use { it.readText() }
            val dataset = parseAuditDataset(rawJson)
            cachedDataset = dataset
            return dataset
        }
    }
}

internal fun parseAuditDataset(rawJson: String): AuditDataset {
    val root = JSONObject(rawJson)
    val zonesJson = root.getJSONArray("zones")
    val circlesJson = root.getJSONArray("circles")
    val recordsJson = root.getJSONObject("data")

    val zones = List(zonesJson.length()) { index -> zonesJson.getString(index).trim() }
    val circles = List(circlesJson.length()) { index -> circlesJson.getString(index).trim() }
    val records = buildMap(recordsJson.length()) {
        val keys = recordsJson.keys()
        while (keys.hasNext()) {
            val tin = keys.next()
            val value = recordsJson.getJSONArray(tin)
            put(
                tin,
                AuditRecord(
                    zoneIndex = value.getInt(0),
                    circleIndex = value.getInt(1),
                    category = value.getString(2),
                    assessmentYear = value.getString(3),
                    isSelected = value.getInt(4) == 1
                )
            )
        }
    }

    return AuditDataset(
        zones = zones,
        circles = circles,
        records = records
    )
}
