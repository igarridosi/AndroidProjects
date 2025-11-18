package com.example.oroiapp.ui

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.example.oroiapp.model.BillingCycle
import com.example.oroiapp.viewmodel.AddEditViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch
import com.example.oroiapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    viewModel: AddEditViewModel,
    onNavigateBack: () -> Unit
) {
    val formState by viewModel.formState.collectAsState()

    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    var isSaving by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gehitu Harpidetza") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atzera")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = formState.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Izena (Ad: Netflix)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = formState.amount,
                onValueChange = viewModel::onAmountChange,
                label = { Text("Kopurua (Ad: 9.99)") },
                modifier = Modifier.fillMaxWidth()
            )

            BillingCycleSelector(selectedCycle = formState.billingCycle, onCycleSelected = viewModel::onBillingCycleChange)
            DatePickerField(selectedDate = formState.firstPaymentDate, onDateSelected = viewModel::onDateChange)

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (!isSaving) {
                        scope.launch {
                            isSaving = true // 1. Desgaitu botoia
                            focusManager.clearFocus() // 2. Ezkutatu teklatua
                            viewModel.saveSubscription() // 3. Gorde (eta itxaron)
                            onNavigateBack() // 4. Nabigatu
                        }
                    }
                },
                enabled = !isSaving, // Botoiaren egoera lotu
                modifier = Modifier.fillMaxWidth()
            ) { Text("Gorde Harpidetza") }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingCycleSelector(
    selectedCycle: BillingCycle,
    onCycleSelected: (BillingCycle) -> Unit,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors()
) {
    var expanded by remember { mutableStateOf(false) }
    val cycleOptions = BillingCycle.values()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = when (selectedCycle) {
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
                            },
                            color = Color.Black
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
    onDateSelected: (Date) -> Unit,
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
        modifier = Modifier.fillMaxWidth(),
    )
}