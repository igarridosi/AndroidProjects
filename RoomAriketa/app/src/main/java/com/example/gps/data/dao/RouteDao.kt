package com.example.gps.data.dao

import androidx.room.*
import com.example.gps.data.entities.GpsPoint
import com.example.gps.data.entities.Route
import com.example.gps.data.entities.RouteWithPoints
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {

    // --- Insert ---
    // Suspend erabiltzen dugu eragiketa hauek blokeatzaileak direlako eta background thread-ean joan behar direlako
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoute(route: Route): Long // ID berria itzultzen du

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGpsPoint(point: GpsPoint)

    // --- Delete ---
    @Delete
    suspend fun deleteRoute(route: Route)

    // --- Queries ---

    // Ibilbide guztiak lortu. Flow erabiltzen dugu datuak denbora errealean eguneratzeko UI-an.
    // Ez du 'suspend' behar Flow itzultzen duelako (jadanik asinkronoa da).
    @Transaction // Beharrezkoa da erlazioak kargatzeko
    @Query("SELECT * FROM routes ORDER BY creationDate DESC")
    fun getAllRoutes(): Flow<List<RouteWithPoints>>

    // Ibilbide bat bere puntuekin lortu.
    // Transaction beharrezkoa da Room-ek bi query exekutatzen dituelako (bata Route, bestea Points) atomikoki.
    @Transaction
    @Query("SELECT * FROM routes WHERE routeId = :routeId")
    fun getRouteWithPoints(routeId: Long): Flow<RouteWithPoints>

    @Update
    suspend fun updateRoute(route: Route)

    @Update
    suspend fun updateGpsPoint(point: GpsPoint)
}