package com.example.brainmaster.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.brainmaster.R
import com.example.brainmaster.databinding.FragmentMainMenuBinding

class MainMenuFragment : Fragment() {

    // 1. Preparamos el View Binding, igual que antes.
    private var _binding: FragmentMainMenuBinding? = null
    private val binding get() = _binding!!

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

        // 3. Configuramos el click del botón.
        binding.buttonStart.setOnClickListener {
            // Usamos el NavController para navegar a la siguiente pantalla.
            // El ID de la acción es el que definimos en el nav_graph.xml.
            findNavController().navigate(R.id.action_mainMenuFragment_to_quizFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 4. Limpiamos el binding.
        _binding = null
    }
}