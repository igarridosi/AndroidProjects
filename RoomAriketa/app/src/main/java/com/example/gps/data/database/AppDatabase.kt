package com.example.gps.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.gps.data.dao.RouteDao
import com.example.gps.data.entities.GpsPoint
import com.example.gps.data.entities.Route

@Database(entities = [Route::class, GpsPoint::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun routeDao(): RouteDao

    companion object {
        // Volatile: Hari guztiek berehala ikusiko dute aldaketa
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "track_my_route_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}