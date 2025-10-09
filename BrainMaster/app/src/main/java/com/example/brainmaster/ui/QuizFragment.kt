package com.example.brainmaster.ui

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        // Empezamos a observar los LiveData del ViewModel.
        observeViewModel()

        // Configuramos los clics de los botones.
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

        viewModel.answers.observe(viewLifecycleOwner) { answers ->
            // Asignamos el texto a cada botón.
            binding.buttonAnswer1.text = Html.fromHtml(answers[0], Html.FROM_HTML_MODE_COMPACT)
            binding.buttonAnswer2.text = Html.fromHtml(answers[1], Html.FROM_HTML_MODE_COMPACT)
            binding.buttonAnswer3.text = Html.fromHtml(answers[2], Html.FROM_HTML_MODE_COMPACT)
            binding.buttonAnswer4.text = Html.fromHtml(answers[3], Html.FROM_HTML_MODE_COMPACT)
        }

        viewModel.score.observe(viewLifecycleOwner) { score ->
            // Actualizamos el texto de la puntuación.
            binding.textViewScore.text = "Puntuación: $score"
        }

        viewModel.isGameFinished.observe(viewLifecycleOwner) { isFinished ->
            if (isFinished) {
                // Si el juego ha terminado, navegamos a la pantalla de resultados.
                findNavController().navigate(R.id.action_quizFragment_to_resultFragment)
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
}