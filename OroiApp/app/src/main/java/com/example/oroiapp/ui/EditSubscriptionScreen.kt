package com.example.oroiapp.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
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
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    var isSaving by remember { mutableStateOf(false) }

    val customTextFieldColors = OutlinedTextFieldDefaults.colors(
        unfocusedTextColor = Color.Black,
        focusedTextColor = Color.Black,

        unfocusedTrailingIconColor = Color.Black,
        focusedTrailingIconColor = Color.Black,

        unfocusedLabelColor = Color.Black,
        focusedLabelColor = MaterialTheme.colorScheme.primary,

        unfocusedBorderColor = MaterialTheme.colorScheme.primary,

        unfocusedContainerColor = Color(0xFFFFFFFF).copy(alpha = 0.5f),
        focusedContainerColor = Color(0xFFFFFFFF).copy(alpha = 0.5f)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editatu Harpidetza") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atzera")
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedTextField(
                value = formState.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Izena") },
                modifier = Modifier.fillMaxWidth(),
                colors = customTextFieldColors
            )
            OutlinedTextField(
                value = formState.amount,
                onValueChange = viewModel::onAmountChange,
                label = { Text("Kopurua") },
                modifier = Modifier.fillMaxWidth(),
                colors = customTextFieldColors
            )
            BillingCycleSelector(
                selectedCycle = formState.billingCycle,
                onCycleSelected = viewModel::onBillingCycleChange,
                colors = customTextFieldColors
            )
            DatePickerField(
                selectedDate = formState.firstPaymentDate,
                onDateSelected = viewModel::onDateChange,
                colors = customTextFieldColors
            )

            Spacer(modifier = Modifier.weight(1f))

            // Gordetzeko botoia
            Button(
                onClick = {
                    if (!isSaving) {
                        scope.launch {
                            isSaving = true
                            focusManager.clearFocus()
                            viewModel.saveSubscription()
                            isSaving = false
                            onNavigateBack()
                        }
                    }
                },
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Gorde Aldaketak")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Ezabatu botoia
            OutlinedButton(
                onClick = {
                    if (!isSaving) {
                        scope.launch {
                            isSaving = true
                            focusManager.clearFocus()
                            viewModel.deleteSubscription()
                            isSaving = false
                            onNavigateBack()
                        }
                    }
                },
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
            ) {
                Text("Ezabatu Harpidetza")
            }
        }
    }
}


