package com.froztlass.airquality

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

object NotificationHelper {
    fun createNotification(context: Context, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("default", "Default Channel", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(context, "default")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)

        notificationManager.notify(0, notificationBuilder.build())
    }
}
