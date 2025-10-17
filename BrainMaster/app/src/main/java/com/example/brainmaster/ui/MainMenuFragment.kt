package com.example.brainmaster.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.brainmaster.R
import com.example.brainmaster.databinding.FragmentMainMenuBinding

class MainMenuFragment : Fragment() {

    // 1. Preparamos el View Binding, igual que antes.
    private var _binding: FragmentMainMenuBinding? = null
    private val binding get() = _binding!!

    // Mapa para convertir el nombre de la categoría a su ID numérico de la API
    private val categoryMap = mapOf(
        "General Knowlage" to 9,
        "Movies" to 11,
        "Music" to 12,
        "Video Games" to 15,
        "Science and nature" to 17,
        "Sports" to 21,
        "Geography" to 22
    )

    private val difficultyMap = mapOf(
        "Easy" to "easy",
        "Medium" to "medium",
        "Hard" to "hard",
        "Leo Messi" to "hard"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 2. "Inflamos" el layout y configuramos el binding.
        _binding = FragmentMainMenuBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- Configuración del Menú Desplegable ---
        // 1. Obtenemos la lista de categorías desde strings.xml
        val categories = resources.getStringArray(R.array.quiz_categories)
        val difficulties = resources.getStringArray(R.array.quiz_difficulty)

        // 2. Creamos un Adapter, que es el puente entre la lista y la UI del menú
        val adapterCategory =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
        val adapterDifficulty =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, difficulties)

        // 3. Asignamos el adapter a nuestro AutoCompleteTextView
        binding.autoCompleteTextView1.setAdapter(adapterCategory)
        binding.autoCompleteTextView2.setAdapter(adapterDifficulty)


        // --- Configuración del Clic del Botón ---
        binding.buttonStart.setOnClickListener {
            // 1. Leemos el texto seleccionado en el menú
            val selectedCategoryName = binding.autoCompleteTextView1.text.toString()
            val finalCategoryName = selectedCategoryName.ifBlank { "General Knolage" }
            val selectedDifficultyName = binding.autoCompleteTextView2.text.toString()

            // 2. Buscamos su ID en el mapa. Si no se ha elegido nada, usamos 9 (Conocimiento General) por defecto.
            val selectedCategoryId = categoryMap[selectedCategoryName] ?: 9
            val selectedDifficulty = difficultyMap[selectedDifficultyName] ?: "easy"

            // 3. Creamos un "paquete" (Bundle) para enviar el ID a la siguiente pantalla
            val bundle = bundleOf(
                "CATEGORY_ID" to selectedCategoryId,
                "CATEGORY_NAME" to finalCategoryName,
                "DIFFICULTY" to selectedDifficulty
            )
            Log.v("Difficulty", selectedDifficulty.toString())

            // 4. Navegamos, pasando el paquete con los datos
            findNavController().navigate(R.id.action_mainMenuFragment_to_quizFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 4. Limpiamos el binding.
        _binding = null
    }
}