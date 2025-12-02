package com.example.gps.ui.viewmodel

import androidx.lifecycle.*
import com.example.gps.data.entities.GpsPoint
import com.example.gps.data.entities.Route
import com.example.gps.data.entities.RouteWithPoints
import com.example.gps.data.repository.RouteRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: RouteRepository) : ViewModel() {

    // UI-ak aldagai hau behatuko du (observe)
    // asLiveData erabiltzen dugu Flow-a LiveData bihurtzeko erraztasunagatik XML-ekin,
    // baina StateFlow ere erabil daiteke.
    val allRoutes: LiveData<List<RouteWithPoints>> = repository.allRoutes.asLiveData()

    fun createRouteWithPoint(name: String, lat: Double, lon: Double) {
        viewModelScope.launch {
            // Lehenik ibilbidea sortu eta IDa jaso
            val newRoute = Route(name = name, creationDate = System.currentTimeMillis())
            val routeId = repository.insertRouteReturnId(newRoute)

            // Gero puntua sortu ID hori erabiliz
            val newPoint = GpsPoint(
                latitude = lat,
                longitude = lon,
                timestamp = System.currentTimeMillis(),
                routeOwnerId = routeId
            )
            repository.insertGpsPoint(newPoint)
        }
    }
    fun updateRouteDetails(route: Route, lastPoint: GpsPoint?, newName: String, newLat: Double, newLon: Double) {
        viewModelScope.launch {
            // Izen berria eguneratu
            val updatedRoute = route.copy(name = newName)
            repository.updateRoute(updatedRoute)

            if (lastPoint != null) {
                // Puntua existitzen bada, eguneratu
                val updatedPoint = lastPoint.copy(latitude = newLat, longitude = newLon)
                repository.updateGpsPoint(updatedPoint)
            } else {
                // Punturik ez bazuen, sortu bat berria
                val newPoint = GpsPoint(
                    latitude = newLat,
                    longitude = newLon,
                    timestamp = System.currentTimeMillis(),
                    routeOwnerId = route.routeId
                )
                repository.insertGpsPoint(newPoint)
            }
        }
    }

    fun deleteRoute(route: Route) {
        viewModelScope.launch {
            repository.deleteRoute(route)
        }
    }
}

// ViewModel-ari parametroak (Repository) pasatzeko Factory bat behar dugu
class MainViewModelFactory(private val repository: RouteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}