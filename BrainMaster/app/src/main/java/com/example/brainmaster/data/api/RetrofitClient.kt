package com.example.brainmaster.data.api
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import  com.example.brainmaster.data.TriviaApiService

// Usamos un 'object' para crear un singleton, asegurando que solo haya una
// instancia de Retrofit en toda la app.
object RetrofitClient {
    private const val BASE_URL = "https://opentdb.com/"

    // Usamos 'lazy' para que la inicialización de Retrofit solo ocurra una vez,
    // la primera vez que se acceda a él. Es una forma eficiente y segura.
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // El conversor de JSON a GSON
            .build()
    }

    // Una propiedad pública para que el resto de la app pueda acceder al servicio de la API.
    // También es 'lazy' para que se cree solo cuando se necesite por primera vez.
    val apiService: TriviaApiService by lazy {
        retrofit.create(TriviaApiService::class.java)
    }
}