package com.maruf.bdtaxcalculator.notification

data class AppNotificationItem(
    val id: String,
    val title: String,
    val body: String,
    val receivedAtMillis: Long,
    val source: String,
    val isRead: Boolean
)
