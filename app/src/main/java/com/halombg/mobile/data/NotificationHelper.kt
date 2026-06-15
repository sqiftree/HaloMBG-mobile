package com.halombg.mobile.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

object NotificationHelper {

    private const val GURU_CHANNEL_ID = "guru_notifications"
    private const val SPPG_CHANNEL_ID = "sppg_emergency"

    private fun init(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Guru Channel
            val guruChannel = NotificationChannel(
                GURU_CHANNEL_ID,
                "Ulasan Baru Guru",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifikasi ulasan baru dari siswa"
            }
            manager.createNotificationChannel(guruChannel)

            // SPPG Emergency Channel
            val sppgChannel = NotificationChannel(
                SPPG_CHANNEL_ID,
                "Peringatan Darurat SPPG",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi ulasan gizi kritis dari siswa"
            }
            manager.createNotificationChannel(sppgChannel)
        }
    }

    fun showGuruNewReviewNotification(context: Context, studentName: String, content: String) {
        init(context)
        val builder = NotificationCompat.Builder(context, GURU_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Ulasan Baru Masuk (Guru)")
            .setContentText("Siswa $studentName mengirim ulasan baru.")
            .setStyle(NotificationCompat.BigTextStyle().bigText("Ulasan baru dari $studentName:\n\"$content\""))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), builder.build())
    }

    fun showSppgEmergencyNotification(context: Context, studentName: String, criticalWord: String, content: String) {
        init(context)
        val builder = NotificationCompat.Builder(context, SPPG_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Peringatan Darurat SPPG ⚠")
            .setContentText("Mendeteksi kata kritis '$criticalWord' pada ulasan siswa!")
            .setStyle(NotificationCompat.BigTextStyle().bigText("Peringatan ulasan kritis dari $studentName (Terdeteksi: $criticalWord):\n\"$content\""))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setAutoCancel(true)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}
