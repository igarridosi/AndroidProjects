package com.example.oroiapp.ui

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.oroiapp.model.BillingCycle
import com.example.oroiapp.viewmodel.AddEditViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.oroiapp.ui.theme.OroiTheme
import com.example.oroiapp.viewmodel.SubscriptionFormState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    viewModel: AddEditViewModel,
    onNavigateBack: () -> Unit // Aurreko pantailara itzultzeko
) {
    val formState by viewModel.formState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gehitu Harpidetza") },
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
            OutlinedTextField(
                value = formState.name,
                onValueChange = { viewModel.onNameChange(it) },
                label = { Text("Izena (Ad: Netflix)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = formState.amount,
                onValueChange = { viewModel.onAmountChange(it) },
                label = { Text("Kopurua (Ad: 9.99)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            BillingCycleSelector(
                selectedCycle = formState.billingCycle,
                onCycleSelected = { viewModel.onBillingCycleChange(it) }
            )

            DatePickerField(
                selectedDate = formState.firstPaymentDate,
                onDateSelected = { viewModel.onDateChange(it) }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.saveSubscription {
                        onNavigateBack() // Gorde ondoren, atzera nabigatu
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Gorde Harpidetza")
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingCycleSelector(
    selectedCycle: BillingCycle,
    onCycleSelected: (BillingCycle) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val cycleOptions = BillingCycle.values()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = when(selectedCycle) {
                BillingCycle.WEEKLY -> "Astero"
                BillingCycle.MONTHLY -> "Hilero"
                BillingCycle.ANNUAL -> "Urtero"
            },
            onValueChange = {},
            readOnly = true,
            label = { Text("Fakturazio Zikloa") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            cycleOptions.forEach { cycle ->
                DropdownMenuItem(
                    text = {
                        Text(
                            when (cycle) {
                                BillingCycle.WEEKLY -> "Astero"
                                BillingCycle.MONTHLY -> "Hilero"
                                BillingCycle.ANNUAL -> "Urtero"
                            }
                        )
                    },
                    onClick = {
                        onCycleSelected(cycle)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun DatePickerField(
    selectedDate: Date,
    onDateSelected: (Date) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    calendar.time = selectedDate

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val dateFormat = remember { SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()) }

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
            val newCalendar = Calendar.getInstance()
            newCalendar.set(selectedYear, selectedMonth, selectedDay)
            onDateSelected(newCalendar.time)
        }, year, month, day
    )

    OutlinedTextField(
        value = dateFormat.format(selectedDate),
        onValueChange = {},
        readOnly = true,
        label = { Text("Lehen Ordainketa Eguna") },
        trailingIcon = {
            Icon(Icons.Default.DateRange, "Hautatu data", Modifier.clickable { datePickerDialog.show() })
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, widthDp = 360, heightDp = 740)
@Composable
fun AddEditScreenPreview() {
    // 1. Sortu formularioaren egoera faltsu bat
    val sampleFormState = SubscriptionFormState(
        name = "Scribd",
        amount = "11.99",
        billingCycle = BillingCycle.MONTHLY,
        firstPaymentDate = Date()
    )

    OroiTheme {
        // 2. Berreraiki pantailaren egitura datu estatikoekin eta funtzio hutsekin
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Gehitu Harpidetza") },
                    navigationIcon = { IconButton(onClick = {}) { Icon(Icons.Filled.ArrowBack, "") } }
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
                OutlinedTextField(
                    value = sampleFormState.name,
                    onValueChange = {},
                    label = { Text("Izena (Ad: Netflix)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = sampleFormState.amount,
                    onValueChange = {},
                    label = { Text("Kopurua (Ad: 9.99)") },
                    modifier = Modifier.fillMaxWidth()
                )
                BillingCycleSelector(
                    selectedCycle = sampleFormState.billingCycle,
                    onCycleSelected = {}
                )
                DatePickerField(
                    selectedDate = sampleFormState.firstPaymentDate,
                    onDateSelected = {}
                )
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Gorde Harpidetza")
                }
            }
        }
    }
}