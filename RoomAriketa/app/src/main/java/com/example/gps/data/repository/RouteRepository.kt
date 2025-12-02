package com.example.gps.data.repository

import com.example.gps.data.dao.RouteDao
import com.example.gps.data.entities.GpsPoint
import com.example.gps.data.entities.Route
import com.example.gps.data.entities.RouteWithPoints
import kotlinx.coroutines.flow.Flow

class RouteRepository(private val routeDao: RouteDao) {

    // Flow datuak zuzenean pasatzen ditugu
    val allRoutes: Flow<List<RouteWithPoints>> = routeDao.getAllRoutes()

    fun getRouteDetails(routeId: Long): Flow<RouteWithPoints> {
        return routeDao.getRouteWithPoints(routeId)
    }

    // Suspend funtzioak deitzen ditugu
    suspend fun insertRoute(route: Route) {
        routeDao.insertRoute(route)
    }

    suspend fun insertGpsPoint(point: GpsPoint) {
        routeDao.insertGpsPoint(point)
    }

    suspend fun deleteRoute(route: Route) {
        routeDao.deleteRoute(route)
    }

    suspend fun insertRouteReturnId(route: Route): Long {
        return routeDao.insertRoute(route)
    }

    suspend fun updateRoute(route: Route) {
        routeDao.updateRoute(route)
    }

    suspend fun updateGpsPoint(point: GpsPoint) {
        routeDao.updateGpsPoint(point)
    }
}