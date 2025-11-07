package com.example.oroiapp

import android.app.Application
import androidx.room.Room
import com.example.oroiapp.data.AppDatabase
import com.example.oroiapp.data.SubscriptionDao
import com.example.oroiapp.viewmodel.OroiViewModelFactory

class OroiApplication : Application() {
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
    }
}
