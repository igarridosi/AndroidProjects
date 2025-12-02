package com.example.gps.data.entities

import androidx.room.Embedded
import androidx.room.Relation

data class RouteWithPoints(
    @Embedded val route: Route,
    @Relation(
        parentColumn = "routeId",
        entityColumn = "routeOwnerId"
    )
    val points: List<GpsPoint>
)