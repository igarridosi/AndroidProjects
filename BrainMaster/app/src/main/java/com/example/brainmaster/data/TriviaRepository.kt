package com.example.brainmaster.data

import android.util.Log
import com.example.brainmaster.data.api.RetrofitClient

class TriviaRepository {

    // Obtenemos la instancia del servicio de la API desde nuestro singleton.
    private val apiService = RetrofitClient.apiService

    suspend fun getTriviaQuestions(categoryId: Int, difficulty: String): List<Question> {
        return try {
            // Hacemos la llamada a la API con parámetros de ejemplo.
            val response = apiService.getQuestions(
                amount = 10,
                category = categoryId,
                difficulty = difficulty,
                type = "multiple"
            )

            if (response.isSuccessful) {
                val body = response.body()
                // Si es nulo por alguna razón, devolvemos una lista vacía.
                body?.results ?: emptyList()
            } else {
                // Si la respuesta no fue exitosa (ej: error 404, 500), lo registramos.
                Log.e("TriviaRepository", "API Error: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            // Si ocurre una excepción (ej: no hay internet), la registramos.
            Log.e("TriviaRepository", "Network Error: ${e.message}")
            emptyList()
        }
    }
}