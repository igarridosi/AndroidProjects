package com.example.gps.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "gps_points",
    foreignKeys = [
        ForeignKey(
            entity = Route::class,
            parentColumns = ["routeId"],
            childColumns = ["routeOwnerId"],
            onDelete = ForeignKey.CASCADE // Ibilbidea ezabatzean, puntuak ere ezabatu
        )
    ],
    // Index bat gehitzea gomendagarria da bilaketak azkartzeko FK erabiliz
    indices = [Index(value = ["routeOwnerId"])]
)
data class GpsPoint(
    @PrimaryKey(autoGenerate = true)
    val pointId: Long = 0,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val routeOwnerId: Long // FK erreferentzia
)