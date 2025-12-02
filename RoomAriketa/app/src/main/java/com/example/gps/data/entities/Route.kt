package com.example.gps.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routes")
data class Route(
    @PrimaryKey(autoGenerate = true)
    val routeId: Long = 0, // 0 jartzen dugu, Room-ek autogeneratuko baitu
    val name: String,
    val creationDate: Long // Timestamp moduan gordeko dugu erraztasunagatik
)