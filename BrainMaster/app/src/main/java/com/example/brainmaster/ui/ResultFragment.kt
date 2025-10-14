package com.example.brainmaster.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.brainmaster.R
import com.example.brainmaster.databinding.FragmentResultBinding

class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Recibimos la puntuación enviada desde el QuizFragment.
        val finalScore = arguments?.getInt("FINAL_SCORE") ?: 0

        // 2. Actualizamos el texto de la puntuación.
        binding.textViewFinalScore.text = "Your score: $finalScore / 10"

        // 3. Usamos 'when' para decidir qué imagen mostrar.
        //    'when' es una forma más potente y legible de hacer 'if-else if-else'.
        val imageResource = when (finalScore) {
            in 8..10 -> R.drawable.excellent
            in 6..7 -> R.drawable.notbad
            in 4..5 -> R.drawable.meh
            in 2..3 -> R.drawable.sad
            else -> R.drawable.cry // Cubre los casos 0 y 1
        }

        // 4. Establecemos la imagen decidida en el ImageView.
        binding.resultImageView.setImageResource(imageResource)

        // 5. Configuramos el botón para volver a jugar.
        binding.buttonPlayAgain.setOnClickListener {
            findNavController().navigate(R.id.action_resultFragment_to_mainMenuFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}