package com.example.oroiapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.room.Room
import com.example.oroiapp.data.AppDatabase
import com.example.oroiapp.data.SubscriptionDao
import com.example.oroiapp.viewmodel.OroiViewModelFactory

class OroiApplication : Application() {
    companion object {
        const val CHANNEL_ID = "subscription_reminders"
    }

    override fun onCreate() {
        super.onCreate()
        // Datu-basea sortu
        val database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "oroi_database"
        ).build()

        // Factory-ari DAO-aren instantzia eman aplikazioa hasten denean
        OroiViewModelFactory.dao = database.subscriptionDao()

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Harpidetzen Gogorarazpenak"
            val descriptionText = "Harpidetzak berritu baino lehen abisuak jasotzeko kanala."
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Erregistratu kanala sistemarekin
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
