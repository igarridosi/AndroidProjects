package com.example.brainmaster.data
import com.google.gson.annotations.SerializedName // ¡Importante añadir esta línea!

data class Question(
    val category: String,
    val type: String,
    val difficulty: String,
    val question: String,

    @SerializedName("correct_answer") // Le decimos a GSON cómo se llama el campo en el JSON
    val correctAnswer: String,

    @SerializedName("incorrect_answers") // Hacemos lo mismo para este campo
    val incorrectAnswers: List<String>
)