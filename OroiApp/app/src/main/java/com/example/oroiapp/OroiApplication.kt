package com.example.oroiapp

import android.app.Application
import androidx.room.Room
import com.example.oroiapp.data.AppDatabase
import com.example.oroiapp.viewmodel.OroiViewModelFactory

class OroiApplication : Application() {
    // Datu-basearen instantzia "lazy" erabiliz sortuko dugu,
    // lehen aldiz behar denean bakarrik eraikitzeko.
    private val database by lazy {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "oroi_database"
        ).build()
    }

    // DAO-aren instantzia
    private val dao by lazy {
        database.subscriptionDao()
    }

    // Gure ViewModelFactory-aren instantzia
    val viewModelFactory by lazy {
        OroiViewModelFactory(dao)
    }
}