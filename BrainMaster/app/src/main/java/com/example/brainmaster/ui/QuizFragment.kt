package com.example.brainmaster.ui

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.brainmaster.R
import com.example.brainmaster.databinding.FragmentQuizBinding

class QuizFragment : Fragment() {

    // 'by viewModels()' es una magia de las librerías de Android que se encarga
    // de crear y darnos el ViewModel correcto.
    private val viewModel: QuizViewModel by viewModels()

    // View Binding nos dará acceso directo y seguro a todos los elementos del XML.
    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // "Inflamos" el layout y configuramos el binding.
        // Esto crea la vista a partir del XML y la prepara para ser usada.
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Leemos el ID de la categoría que nos ha enviado el MainMenuFragment
        // Usamos 9 (Conocimiento General) como valor por defecto si algo falla.
        val categoryId = arguments?.getInt("CATEGORY_ID") ?: 9
        val difficulty = arguments?.getString("DIFFICULTY") ?: "easy"

        // 2. Le pasamos el ID al ViewModel para que empiece el juego con la categoría correcta
        viewModel.startGame(categoryId, difficulty)

        // El resto de la función (observadores y clics) no cambia
        observeViewModel()
        setupButtonClicks()
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Mostramos la barra de carga y ocultamos el resto.
            binding.progressBar.isVisible = isLoading
            binding.answersContainer.isVisible = !isLoading
            binding.textViewQuestion.isVisible = !isLoading
        }

        viewModel.currentQuestion.observe(viewLifecycleOwner) { question ->
            resetButtonStates()
            binding.textViewQuestion.text = Html.fromHtml(question.question, Html.FROM_HTML_MODE_COMPACT)
        }

        // Dentro de observeViewModel()
        viewModel.answers.observe(viewLifecycleOwner) { answers ->
            // Asignamos el texto a cada botón PRIMERO
            binding.buttonAnswer1.text = Html.fromHtml(answers[0], Html.FROM_HTML_MODE_COMPACT)
            binding.buttonAnswer2.text = Html.fromHtml(answers[1], Html.FROM_HTML_MODE_COMPACT)
            binding.buttonAnswer3.text = Html.fromHtml(answers[2], Html.FROM_HTML_MODE_COMPACT)
            binding.buttonAnswer4.text = Html.fromHtml(answers[3], Html.FROM_HTML_MODE_COMPACT)

            // AHORA, llamamos a la función de animación
            val buttons = listOf(binding.buttonAnswer1, binding.buttonAnswer2, binding.buttonAnswer3, binding.buttonAnswer4)
            animateAnswerButtons(buttons)
        }

        viewModel.score.observe(viewLifecycleOwner) { score ->
            // Actualizamos el texto de la puntuación.
            binding.textViewScore.text = "Puntuación: $score"
        }

        viewModel.isGameFinished.observe(viewLifecycleOwner) { isFinished ->
            if (isFinished) {
                // 1. Obtenemos la puntuación final desde el ViewModel.
                // El '?: 0' es una seguridad por si el valor fuera nulo.
                val finalScore = viewModel.score.value ?: 0

                // 2. Creamos un "paquete" para enviar los datos.
                val bundle = bundleOf("FINAL_SCORE" to finalScore)

                // 3. Navegamos, pasando el paquete.
                findNavController().navigate(R.id.action_quizFragment_to_resultFragment, bundle)
            }
        }
        viewModel.answerResult.observe(viewLifecycleOwner) { result ->
            showAnswerFeedback(result)
        }
    }

    private fun setupButtonClicks() {
        binding.buttonAnswer1.setOnClickListener {
            viewModel.onAnswerSelected(binding.buttonAnswer1.text.toString())
        }
        binding.buttonAnswer2.setOnClickListener {
            viewModel.onAnswerSelected(binding.buttonAnswer2.text.toString())
        }
        binding.buttonAnswer3.setOnClickListener {
            viewModel.onAnswerSelected(binding.buttonAnswer3.text.toString())
        }
        binding.buttonAnswer4.setOnClickListener {
            viewModel.onAnswerSelected(binding.buttonAnswer4.text.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 6. Limpiamos el binding para evitar fugas de memoria.
        _binding = null
    }

    private fun showAnswerFeedback(result: AnswerResult) {
        // Obtenemos una lista de todos los botones para iterar sobre ellos
        val buttons = listOf(binding.buttonAnswer1, binding.buttonAnswer2, binding.buttonAnswer3, binding.buttonAnswer4)

        for (button in buttons) {
            // Deshabilitamos todos los botones para que el usuario no pueda hacer clic de nuevo
            button.isEnabled = false

            val answerText = button.text.toString()

            when {
                // Si el texto del botón es la respuesta correcta, lo pintamos de verde
                answerText == result.correctAnswer -> {
                    button.setBackgroundColor(resources.getColor(R.color.correct_green, null))
                }
                // Si el texto del botón es la que el usuario eligió (y no es la correcta), lo pintamos de rojo
                answerText == result.selectedAnswer -> {
                    button.setBackgroundColor(resources.getColor(R.color.incorrect_red, null))
                }
            }
        }
    }

    private fun resetButtonStates() {
        val buttons = listOf(binding.buttonAnswer1, binding.buttonAnswer2, binding.buttonAnswer3, binding.buttonAnswer4)
        for (button in buttons) {
            // Restauramos el color de fondo por defecto (esto puede variar según tu tema)
            // Una forma simple es usar el color primario de tu app.
            button.setBackgroundColor(resources.getColor(com.google.android.material.R.color.design_default_color_primary, null))
            // Volvemos a habilitar los botones
            button.isEnabled = true
        }
    }

    private fun animateAnswerButtons(buttons: List<View>) {
        val delayBetweenButtons = 150L // Retraso de 150ms entre cada botón

        buttons.forEachIndexed { index, view ->
            // 1. Preparamos el estado inicial de cada botón: invisible y "fuera de la pantalla"
            view.alpha = 0f
            view.translationY = view.height.toFloat() // Lo movemos hacia abajo su propia altura

            // 2. Creamos y configuramos la animación
            view.animate()
                .translationY(0f) // Lo devolvemos a su posición Y original
                .alpha(1f) // Lo hacemos totalmente visible
                .setStartDelay(index * delayBetweenButtons) // ¡La clave del efecto escalonado!
                .setDuration(400L) // Duración de la animación de cada botón
                .setInterpolator(DecelerateInterpolator()) // El mismo efecto suave del XML
                .start()
        }
    }
}