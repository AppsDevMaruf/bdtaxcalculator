package com.maruf.bdtaxcalculator.notification

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

object AppNotificationStore {
    private const val PREFS_NAME = "taxpro_notification_inbox"
    private const val KEY_ITEMS = "items"
    private const val MAX_ITEMS = 50

    private val notificationsFlow = MutableStateFlow<List<AppNotificationItem>>(emptyList())
    private var isHydrated = false

    fun observe(context: Context): StateFlow<List<AppNotificationItem>> {
        hydrateIfNeeded(context.applicationContext)
        return notificationsFlow
    }

    fun addFirebaseNotification(
        context: Context,
        title: String,
        body: String,
        messageId: String? = null,
        receivedAtMillis: Long = System.currentTimeMillis()
    ) {
        val cleanTitle = title.trim().ifEmpty { "BD Tax Calculator Update" }
        val cleanBody = body.trim()
        if (cleanBody.isEmpty()) return

        val item = AppNotificationItem(
            id = messageId?.takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString(),
            title = cleanTitle,
            body = cleanBody,
            receivedAtMillis = receivedAtMillis,
            source = "firebase",
            isRead = false
        )

        update(context.applicationContext) { current ->
            (listOf(item) + current.filterNot { it.id == item.id })
                .sortedByDescending { it.receivedAtMillis }
                .take(MAX_ITEMS)
        }
    }

    fun markAllRead(context: Context) {
        update(context.applicationContext) { current ->
            current.map { it.copy(isRead = true) }
        }
    }

    fun clear(context: Context) {
        update(context.applicationContext) { emptyList() }
    }

    private fun hydrateIfNeeded(context: Context) {
        if (isHydrated) return
        synchronized(this) {
            if (isHydrated) return
            notificationsFlow.value = readItems(context)
            isHydrated = true
        }
    }

    private fun update(
        context: Context,
        transform: (List<AppNotificationItem>) -> List<AppNotificationItem>
    ) {
        hydrateIfNeeded(context)
        val updated = transform(notificationsFlow.value)
        notificationsFlow.value = updated
        writeItems(context, updated)
    }

    private fun readItems(context: Context): List<AppNotificationItem> {
        val raw = prefs(context).getString(KEY_ITEMS, null).orEmpty()
        if (raw.isBlank()) return emptyList()

        return runCatching {
            val jsonArray = JSONArray(raw)
            buildList {
                for (index in 0 until jsonArray.length()) {
                    val item = jsonArray.optJSONObject(index) ?: continue
                    add(
                        AppNotificationItem(
                            id = item.optString("id"),
                            title = item.optString("title"),
                            body = item.optString("body"),
                            receivedAtMillis = item.optLong("receivedAtMillis"),
                            source = item.optString("source", "firebase"),
                            isRead = item.optBoolean("isRead", false)
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
            .filter { it.id.isNotBlank() && it.body.isNotBlank() }
            .sortedByDescending { it.receivedAtMillis }
            .take(MAX_ITEMS)
    }

    private fun writeItems(context: Context, items: List<AppNotificationItem>) {
        val jsonArray = JSONArray()
        items.forEach { item ->
            jsonArray.put(
                JSONObject()
                    .put("id", item.id)
                    .put("title", item.title)
                    .put("body", item.body)
                    .put("receivedAtMillis", item.receivedAtMillis)
                    .put("source", item.source)
                    .put("isRead", item.isRead)
            )
        }
        prefs(context).edit { putString(KEY_ITEMS, jsonArray.toString()) }
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
