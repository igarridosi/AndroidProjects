package com.example.gps.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gps.data.database.AppDatabase
import com.example.gps.data.entities.GpsPoint
import com.example.gps.data.entities.Route
import com.example.gps.data.repository.RouteRepository
import com.example.gps.databinding.ActivityMainBinding
import com.example.gps.ui.viewmodel.MainViewModel
import com.example.gps.ui.viewmodel.MainViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Dependentzien eskuzko injekzioa (Hilt bezalako liburutegiak erabili gabe tutorialerako)
    private val database by lazy { AppDatabase.Companion.getDatabase(this) }
    private val repository by lazy { RouteRepository(database.routeDao()) }

    // ViewModel-a Factory erabiliz hasieratu
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // KLIK EGITEAN: Editatzeko Dialog-a ireki
        val adapter = RouteAdapter { clickedItem -> // clickedItem orain RouteWithPoints motakoa da (Adapterra aldatu genuelako)
            // Route objektua eta bere puntuak banatzen ditugu
            val route = clickedItem.route
            val lastPoint = clickedItem.points.lastOrNull() // Azken puntua hartu (baldin badago)

            showEditRouteDialog(route, lastPoint)
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // Datuak behatu (Observe)
        viewModel.allRoutes.observe(this) { routes ->
            // Datu basea aldatzen denean, hau automatikoki exekutatzen da
            adapter.submitList(routes)
        }

        // BOTOIA SAKATZEAN: Sortzeko Dialog-a ireki
        binding.fabAdd.setOnClickListener {
            showCreateRouteDialog()
        }
    }
    // 1. Sortzeko Dialog-a
    private fun showCreateRouteDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Ibilbide Berria")

        // Inputak sortu
        val (layout, etName, etLat, etLon) = createInputLayout()
        builder.setView(layout)

        builder.setPositiveButton("Sortu") { _, _ ->
            val name = etName.text.toString()
            val latStr = etLat.text.toString()
            val lonStr = etLon.text.toString()

            if (name.isNotEmpty() && latStr.isNotEmpty() && lonStr.isNotEmpty()) {
                try {
                    viewModel.createRouteWithPoint(name, latStr.toDouble(), lonStr.toDouble())
                } catch (e: NumberFormatException) {
                    showToast("Zenbakiak gaizki daude")
                }
            } else {
                showToast("Datu guztiak beharrezkoak dira")
            }
        }
        builder.setNegativeButton("Utzi") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    // 2. Editatzeko Dialog-a
    private fun showEditRouteDialog(route: Route, point: GpsPoint?) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Editatu: ${route.name}")

        val (layout, etName, etLat, etLon) = createInputLayout()

        // Datuak aurrez kargatu (Pre-fill)
        etName.setText(route.name)
        if (point != null) {
            etLat.setText(point.latitude.toString())
            etLon.setText(point.longitude.toString())
        }

        builder.setView(layout)

        // 1. GORDE (Positive)
        builder.setPositiveButton("Gorde") { _, _ ->
            val newName = etName.text.toString()
            val latStr = etLat.text.toString()
            val lonStr = etLon.text.toString()

            if (newName.isNotEmpty() && latStr.isNotEmpty() && lonStr.isNotEmpty()) {
                try {
                    viewModel.updateRouteDetails(route, point, newName, latStr.toDouble(), lonStr.toDouble())
                } catch (e: NumberFormatException) {
                    showToast("Zenbakiak gaizki daude")
                }
            } else {
                showToast("Datu guztiak beharrezkoak dira")
            }
        }

        // 2. EZABATU (Neutral) - FUNTZIO BERRIA
        builder.setNeutralButton("Ezabatu") { _, _ ->
            // Segurtasun mezu bat (Confirmation Dialog) gehi daiteke hemen,
            // baina oraingoz zuzenean ezabatuko dugu.
            viewModel.deleteRoute(route)
            showToast("Ibilbidea ezabatua")
        }

        // 3. UTZI (Negative)
        builder.setNegativeButton("Utzi") { dialog, _ -> dialog.cancel() }

        // Dialog-a sortu eta erakutsi
        val dialog = builder.create()
        dialog.show()

        // TRUKOA: "Ezabatu" botoiaren kolorea gorriz jartzeko
        // Hau 'dialog.show()' ondoren egin behar da derrigorrez
        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEUTRAL).setTextColor(android.graphics.Color.RED)
    }

    // Funtzio laguntzailea: Input diseinua sortzen du kode bidez (XMLrik gabe azkarrago egiteko)
    private fun createInputLayout(): InputViews {
        val context = this
        val layout = android.widget.LinearLayout(context)
        layout.orientation = android.widget.LinearLayout.VERTICAL
        layout.setPadding(50, 40, 50, 10)

        val etName = android.widget.EditText(context)
        etName.hint = "Izena"
        layout.addView(etName)

        val etLat = android.widget.EditText(context)
        etLat.hint = "Latitudea"
        etLat.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL or android.text.InputType.TYPE_NUMBER_FLAG_SIGNED
        layout.addView(etLat)

        val etLon = android.widget.EditText(context)
        etLon.hint = "Longitudea"
        etLon.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL or android.text.InputType.TYPE_NUMBER_FLAG_SIGNED
        layout.addView(etLon)

        return InputViews(layout, etName, etLat, etLon)
    }

    // Datu klase txiki bat View-ak itzultzeko
    data class InputViews(
        val layout: android.view.View,
        val etName: android.widget.EditText,
        val etLat: android.widget.EditText,
        val etLon: android.widget.EditText
    )

    private fun showToast(msg: String) {
        android.widget.Toast.makeText(this, msg, android.widget.Toast.LENGTH_SHORT).show()
    }
}