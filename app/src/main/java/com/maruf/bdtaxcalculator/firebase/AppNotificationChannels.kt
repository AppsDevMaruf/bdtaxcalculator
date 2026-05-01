package com.maruf.bdtaxcalculator.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.content.getSystemService
import com.maruf.bdtaxcalculator.R

object AppNotificationChannels {
    const val DEFAULT_CHANNEL_ID = "taxpro_general_updates"

    fun createDefaultChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            DEFAULT_CHANNEL_ID,
            context.getString(R.string.default_notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = context.getString(R.string.default_notification_channel_description)
        }

        context.getSystemService<NotificationManager>()
            ?.createNotificationChannel(channel)
    }
}
