package com.example.brainmaster.ui

import android.util.Log
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
    val questionCounterText = MutableLiveData<String>()
    val isFiftyFiftyAvailable = MutableLiveData<Boolean>(true)
    val isSwapAvailable = MutableLiveData<Boolean>(true)
    val answersToHide = MutableLiveData<List<String>>()

    val answerResult = MutableLiveData<AnswerResult>()

    // 2. Propiedades internas para manejar el estado del juego.
    private val repository = TriviaRepository()
    private var allQuestions: List<Question> = emptyList()
    private var currentQuestionIndex: Int = 0

    private var currentCategoryId: Int = 9
    private var currentDifficulty: String = "medium"

    /*
    // 3. 'init' es un bloque que se ejecuta en cuanto se crea el ViewModel.
    //    Es el lugar perfecto para empezar a cargar las preguntas.
    init {
        startGame()
    }

     */

    // 4. Función para empezar o reiniciar el juego.
    fun startGame(categoryId: Int, difficulty: String) {
        this.currentCategoryId = categoryId
        this.currentDifficulty = difficulty

        currentQuestionIndex = 0
        score.value = 0 // Ponemos el contenido de la caja "score" a 0
        isGameFinished.value = false // El juego no ha terminado

        // Usamos viewModelScope.launch para hacer la llamada a la red en segundo plano
        // sin bloquear la app. Es la forma moderna y segura de hacerlo.
        viewModelScope.launch {
            isLoading.value = true // Avisamos a la pantalla que estamos cargando
            allQuestions = repository.getTriviaQuestions(categoryId, difficulty)

            /*
            // DEBUGGER
            Log.d("API_DATA_CHECK", "--- Preguntas Recibidas de la API ---")
            allQuestions.forEachIndexed { index, question ->
                // Imprimimos la respuesta correcta EXACTA que nos dio la API
                Log.d("API_DATA_CHECK", "Pregunta ${index + 1}: '${question.question}' -> Respuesta Correcta de la API: '${question.correctAnswer}'")
            }
             */

            if (allQuestions.isNotEmpty()) {
                // Mostramos las preguntas
                showNextQuestion()
            }
            isLoading.value = false // Avisamos que hemos terminado de cargar
        }

        // PowerUps
        isFiftyFiftyAvailable.value = true
        isSwapAvailable.value = true
    }

    // 5. Función que prepara la siguiente pregunta para la pantalla.
    private fun showNextQuestion() {
        val question = allQuestions[currentQuestionIndex]
        currentQuestion.value = question // Ponemos la pregunta en su "caja"

        // Creamos la lista de respuestas y la mezclamos
        val answerList = question.incorrectAnswers.toMutableList()
        answerList.add(question.correctAnswer)
        answers.value = answerList.shuffled() // Ponemos la lista mezclada en su "caja"

        questionCounterText.value = "Question ${currentQuestionIndex+1}/${allQuestions.size}"
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

    fun useFiftyFifty() {
        if (isFiftyFiftyAvailable.value != true) {
            return
        }
        isFiftyFiftyAvailable.value = false

        if (allQuestions.isNotEmpty()) {
            //    Accedemos a la pregunta actual y cogemos su lista de respuestas incorrectas.
            val currentQuestion = allQuestions[currentQuestionIndex]
            val incorrectAnswers = currentQuestion.incorrectAnswers

            //    '.shuffled()' es una función mágica de Kotlin que desordena la lista.
            //    '.take(2)' coge los dos primeros elementos de esa lista ya desordenada.
            //    Esto nos da dos respuestas incorrectas aleatorias de forma muy sencilla.
            val answersToRemove = incorrectAnswers.shuffled().take(2)

            //    Actualizamos el valor de nuestro LiveData 'answersToHide'.
            //    El QuizFragment reaccionará ocultando los botones que contengan estos textos.
            answersToHide.value = answersToRemove
        }
    }

    fun useSwapQuestion() {
        if (isSwapAvailable.value != true) {
            return
        }
        isSwapAvailable.value = false

        viewModelScope.launch {
            // Le pasamos la categoría y dificultad que hemos guardado
            val newQuestion = repository.getNewQuestion(
                currentQuestions = allQuestions,
                categoryId = currentCategoryId,
                difficulty = currentDifficulty
            )

            if (newQuestion != null) {
                val mutableQuestions = allQuestions.toMutableList()
                mutableQuestions[currentQuestionIndex] = newQuestion
                allQuestions = mutableQuestions.toList()
                showNextQuestion()
            } else {
                isSwapAvailable.value = true // Devolvemos el power-up si falla
            }
        }
    }
}