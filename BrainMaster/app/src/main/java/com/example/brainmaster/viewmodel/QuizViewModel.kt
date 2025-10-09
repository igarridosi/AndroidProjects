package com.example.brainmaster.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.brainmaster.data.Question
import com.example.brainmaster.data.TriviaRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

data class AnswerResult(val selectedAnswer: String, val correctAnswer: String)
class QuizViewModel : ViewModel() {

    // 1. Creamos las "cajas" (MutableLiveData) que la pantalla observará.
    val isLoading = MutableLiveData<Boolean>()
    val currentQuestion = MutableLiveData<Question>()
    val answers = MutableLiveData<List<String>>()
    val score = MutableLiveData<Int>()
    val isGameFinished = MutableLiveData<Boolean>()

    val answerResult = MutableLiveData<AnswerResult>()

    // 2. Propiedades internas para manejar el estado del juego.
    private val repository = TriviaRepository()
    private var allQuestions: List<Question> = emptyList()
    private var currentQuestionIndex: Int = 0

    // 3. 'init' es un bloque que se ejecuta en cuanto se crea el ViewModel.
    //    Es el lugar perfecto para empezar a cargar las preguntas.
    init {
        startGame()
    }

    // 4. Función para empezar o reiniciar el juego.
    fun startGame() {
        currentQuestionIndex = 0
        score.value = 0 // Ponemos el contenido de la caja "score" a 0
        isGameFinished.value = false // El juego no ha terminado

        // Usamos viewModelScope.launch para hacer la llamada a la red en segundo plano
        // sin bloquear la app. Es la forma moderna y segura de hacerlo.
        viewModelScope.launch {
            isLoading.value = true // Avisamos a la pantalla que estamos cargando
            allQuestions = repository.getTriviaQuestions()

            if (allQuestions.isNotEmpty()) {
                // Mostramos las preguntas
                showNextQuestion()
            }
            isLoading.value = false // Avisamos que hemos terminado de cargar
        }
    }

    // 5. Función que prepara la siguiente pregunta para la pantalla.
    private fun showNextQuestion() {
        val question = allQuestions[currentQuestionIndex]
        currentQuestion.value = question // Ponemos la pregunta en su "caja"

        // Creamos la lista de respuestas y la mezclamos
        val answerList = question.incorrectAnswers.toMutableList()
        answerList.add(question.correctAnswer)
        answers.value = answerList.shuffled() // Ponemos la lista mezclada en su "caja"
    }

    // 6. Función que se llama cuando el usuario elige una respuesta.
    fun onAnswerSelected(selectedAnswer: String) {
        val question = allQuestions[currentQuestionIndex]
        val correctAnswer = question.correctAnswer

        // 1. Informamos al Fragment sobre el resultado para que pinte los colores.
        answerResult.value = AnswerResult(selectedAnswer, correctAnswer)

        // 2. Comprobamos si la respuesta es correcta y actualizamos la puntuación.
        if (selectedAnswer == correctAnswer) {
            score.value = (score.value ?: 0) + 1
        }

        // 3. Lanzamos una nueva corrutina para la pausa.
        viewModelScope.launch {
            delay(2000)

            // 4. Después de la pausa, continuamos con la lógica del juego.
            if (currentQuestionIndex < allQuestions.size - 1) {
                currentQuestionIndex++
                showNextQuestion()
            } else {
                isGameFinished.value = true
            }
        }
    }
}