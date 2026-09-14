package com.maruf.bdtaxcalculator.pdf

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.maruf.bdtaxcalculator.R

internal object PdfDownloadNotification {
    private const val ChannelId = "pdf_downloads"

    /** Notification failures must never turn a successful file save into a failure. */
    fun show(context: Context, uri: Uri, isBangla: Boolean) {
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) return
        val manager = NotificationManagerCompat.from(context)
        if (!manager.areNotificationsEnabled()) return

        runCatching {
            if (Build.VERSION.SDK_INT >= 26) {
                val channel = NotificationChannel(
                    ChannelId,
                    if (isBangla) "PDF ডাউনলোড" else "PDF downloads",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                context.getSystemService(NotificationManager::class.java)
                    .createNotificationChannel(channel)
            }

            // Explicit destination, separate from both the launcher and FCM intents.
            val openIntent = Intent(context, PdfOpenActivity::class.java).apply {
                action = "${context.packageName}.OPEN_DOWNLOADED_PDF"
                data = uri
                putExtra(PdfOpenActivity.ExtraBangla, isBangla)
                clipData = ClipData.newRawUri("PDF", uri)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val openPdf = PendingIntent.getActivity(
                context, 0, openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val notification = NotificationCompat.Builder(context, ChannelId)
                .setSmallIcon(R.drawable.ic_pdf_download_notification)
                .setContentTitle(if (isBangla) "PDF ডাউনলোড সম্পন্ন" else "PDF download complete")
                .setContentText(if (isBangla) "রিপোর্ট খুলতে ট্যাপ করুন" else "Tap to open your report")
                .setContentIntent(openPdf)
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_STATUS)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .build()
            manager.notify(uri.toString(), 1, notification)
        }
    }
}
