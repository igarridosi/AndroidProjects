package com.example.oroiapp.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.example.oroiapp.viewmodel.EditSubscriptionViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSubscriptionScreen(
    viewModel: EditSubscriptionViewModel,
    onNavigateBack: () -> Unit
) {

    val formState by viewModel.formState.collectAsState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editatu Harpidetza") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atzera")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // 2. TextField-ek ZUZENEAN ViewModel-aren egoera irakurri eta aldatzen dute.
            OutlinedTextField(
                value = formState.name,
                onValueChange = viewModel::onNameChange, // Ekintza zuzenean ViewModel-era
                label = { Text("Izena") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = formState.amount,
                onValueChange = viewModel::onAmountChange,
                label = { Text("Kopurua") },
                modifier = Modifier.fillMaxWidth()
            )
            BillingCycleSelector(
                selectedCycle = formState.billingCycle,
                onCycleSelected = viewModel::onBillingCycleChange
            )
            DatePickerField(
                selectedDate = formState.firstPaymentDate,
                onDateSelected = viewModel::onDateChange
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.saveSubscription() // Jada ez du callback-ik hartzen
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Gorde Aldaketak") }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.deleteSubscription() // Jada ez du callback-ik hartzen
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
            ) { Text("Ezabatu Harpidetza") }
        }
    }
}