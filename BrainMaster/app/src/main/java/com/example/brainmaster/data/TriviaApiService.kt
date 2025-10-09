package com.example.brainmaster.data
import retrofit2.Response // Importa la clase Response de Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

interface TriviaApiService {

    // Definimos una llamada GET al endpoint "api.php"
    @GET("api.php")
    suspend fun getQuestions(
        @Query("amount") amount: Int,          // Parámetro de la URL: ?amount=10
        @Query("category") category: Int,      // Parámetro: &category=9
        @Query("difficulty") difficulty: String, // Parámetro: &difficulty=medium
        @Query("type") type: String = "multiple" // Parámetro con valor por defecto
    ): Response<TriviaResponse> // La función devuelve un objeto TriviaResponse

}