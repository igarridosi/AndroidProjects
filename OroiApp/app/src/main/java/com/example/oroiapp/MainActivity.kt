package com.example.oroiapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                onNavigateBack = { navController.popBackStack() }
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
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}