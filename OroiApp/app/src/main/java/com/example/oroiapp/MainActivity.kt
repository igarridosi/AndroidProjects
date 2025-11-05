package com.example.oroiapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.oroiapp.ui.AddEditScreen
import com.example.oroiapp.ui.MainScreen
import com.example.oroiapp.ui.theme.OroiTheme
import com.example.oroiapp.viewmodel.AddEditViewModel
import com.example.oroiapp.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OroiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    OroiApp()
                }
            }
        }
    }
}

@Composable
fun OroiApp() {
    // 1. Lortu Application instantzia modu zuzenean Composable baten barruan
    val context = LocalContext.current
    val application = context.applicationContext as OroiApplication
    val factory = application.viewModelFactory

    // 2. Erabili factory-a ViewModel-ak lortzeko
    val mainViewModel: MainViewModel = viewModel(factory = factory)
    val addEditViewModel: AddEditViewModel = viewModel(factory = factory)

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main_screen") {
        composable("main_screen") {
            MainScreen(
                viewModel = mainViewModel,
                onAddSubscription = {
                    navController.navigate("add_edit_screen")
                }
            )
        }

        composable("add_edit_screen") {
            AddEditScreen(
                viewModel = addEditViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}