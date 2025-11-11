package com.example.oroiapp

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.oroiapp.ui.AddEditScreen
import com.example.oroiapp.ui.EditSubscriptionScreen
import com.example.oroiapp.ui.MainScreen
import com.example.oroiapp.ui.theme.OroiTheme
import com.example.oroiapp.viewmodel.AddEditViewModel
import com.example.oroiapp.viewmodel.EditSubscriptionViewModel
import com.example.oroiapp.viewmodel.MainViewModel
import com.example.oroiapp.viewmodel.OroiViewModelFactory

class MainActivity : ComponentActivity() {
    // 1. Baimena eskatzeko 'launcher'-a sortu
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Baimena onartu da. Mezu txiki bat erakutsi.
            Toast.makeText(this, "Notifikazioak aktibatuta.", Toast.LENGTH_SHORT).show()
        } else {
            // Baimena ukatu da. Mezu luzeago bat erakutsi.
            Toast.makeText(
                this,
                "Baimena ukatu da. Abisuak ez dira bidaliko.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askNotificationPermission()
        setContent {
            OroiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    OroiApp(OroiViewModelFactory)
                }
            }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@Composable
fun OroiApp(factory: ViewModelProvider.Factory) {
    val mainViewModel: MainViewModel = viewModel(factory = factory)
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main_screen") {
        composable("main_screen") {
            MainScreen(
                viewModel = mainViewModel,
                onAddSubscription = { navController.navigate("add_subscription") },
                onEditSubscription = { subscriptionId -> navController.navigate("edit_subscription/$subscriptionId") }
            )
        }

        composable(route = "add_subscription") {
            val addViewModel: AddEditViewModel = viewModel(factory = factory)
            AddEditScreen(
                viewModel = addViewModel,
                onNavigateBack = { navController.navigate("main_screen") {
                    popUpTo("main_screen") { inclusive = true }
                } }
            )
        }

        composable(
            route = "edit_subscription/{subscriptionId}",
            arguments = listOf(navArgument("subscriptionId") { type = NavType.IntType })
        ) { backStackEntry ->

            // 1. Lortu ID-a nabigazio-argudioetatik.
            val subscriptionId = backStackEntry.arguments?.getInt("subscriptionId")

            // 2. Erabili ID hori ViewModel-a sortzeko GAKO (key) gisa.
            //    Horrela, IDa aldatzen den bakoitzean, ViewModel BERRI bat sortuko da.
            val editViewModel: EditSubscriptionViewModel = viewModel(
                key = subscriptionId?.toString(), // GAKOA EZINBESTEKOA DA
                factory = factory
            )

            EditSubscriptionScreen(
                viewModel = editViewModel,
                onNavigateBack = { navController.navigate("main_screen") {
                    popUpTo("main_screen") { inclusive = true }
                } }
            )
        }
    }
}