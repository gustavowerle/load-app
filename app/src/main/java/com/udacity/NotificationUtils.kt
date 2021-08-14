package com.udacity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

private const val CHANNEL_ID = "download_notification"
private const val CHANNEL_NAME = "Download Notification"

fun NotificationManager.createNotification(context: Context, selectedItem: String, status: String) {
    val contentIntent = Intent(context, DetailActivity::class.java).apply {
        putExtra(DetailActivity.SELECTED_ITEM_EXTRA, selectedItem)
        putExtra(DetailActivity.STATUS_EXTRA, status)
    }

    val contentPendingIntent = PendingIntent.getActivity(
        context,
        0,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_baseline_cloud_download_24)
        .setContentTitle(context.getString(R.string.notification_title))
        .setContentText(context.getString(R.string.notification_description))
        .setContentIntent(contentPendingIntent)
        .addAction(
            R.drawable.ic_baseline_cloud_download_24,
            context.resources.getString(R.string.notification_button),
            contentPendingIntent
        )
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)

    notify(0, builder.build())
}

fun NotificationManager.createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )

        notificationChannel.description = context.getString(R.string.notification_description)
        createNotificationChannel(notificationChannel)
    }
}

