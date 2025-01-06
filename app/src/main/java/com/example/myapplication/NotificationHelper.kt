package com.example.myapplication
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import android.app.NotificationChannel
import android.os.Build
// NotificationHelper sınıfı
//class NotificationHelper(private val context: Context) {
//
//    fun createNotification(title: String, content: String) {
//        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        // Kanal sadece bir kez oluşturulmalı
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channelId = "events_channel"
//            val existingChannel = notificationManager.getNotificationChannel(channelId)
//
//            // Eğer kanal yoksa, yeni kanal oluşturulur
//            if (existingChannel == null) {
//                val channel = NotificationChannel(
//                    channelId,
//                    "Etkinlik Bildirimleri",
//                    NotificationManager.IMPORTANCE_DEFAULT
//                )
//                notificationManager.createNotificationChannel(channel)
//            }
//        }
//        // Bildirimi oluştur
//        val notification = NotificationCompat.Builder(context, "events_channel")
//            .setContentTitle(title)
//            .setContentText(content)
//            .setSmallIcon(R.drawable.notification_icon)
//            .build()
//
//        notificationManager.notify(0, notification)  // Bildirimi gönder
//    }
//}
//
//
// NotificationHelper.kt
class NotificationHelper(private val context: Context) {
    fun createNotification(title: String, content: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Kanal sadece bir kez oluşturulmalı
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "event_channel" // Kanal ID'si aynı olmalı
            val existingChannel = notificationManager.getNotificationChannel(channelId)

            if (existingChannel == null) {
                val channel = NotificationChannel(
                    channelId,
                    "Etkinlik Bildirimleri",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }
        }
        // Bildirimi oluştur
        val notification = NotificationCompat.Builder(context, "event_channel") // Kanal ID'si burada da aynı olmalı
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.notification_icon)
            .build()

        notificationManager.notify(0, notification)  // Bildirimi gönder
    }
}