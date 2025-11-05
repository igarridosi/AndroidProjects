package com.example.oroiapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oroiapp.data.SubscriptionDao
import com.example.oroiapp.model.BillingCycle
import com.example.oroiapp.model.Subscription
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

// Formularioko eremuen egoera gordetzeko datu-klasea
data class SubscriptionFormState(
    val name: String = "",
    val amount: String = "",
    val currency: String = "EUR",
    val billingCycle: BillingCycle = BillingCycle.MONTHLY,
    val firstPaymentDate: Date = Date() // Gaurko data lehenetsi bezala
)

class AddEditViewModel(private val subscriptionDao: SubscriptionDao) : ViewModel() {

    private val _formState = MutableStateFlow(SubscriptionFormState())
    val formState = _formState.asStateFlow()

    fun onNameChange(newName: String) {
        _formState.update { it.copy(name = newName) }
    }

    fun onAmountChange(newAmount: String) {
        // Ziurtatu zenbakizko balioa dela soilik
        if (newAmount.isEmpty() || newAmount.matches(Regex("^\\d*\\.?\\d*\$"))) {
            _formState.update { it.copy(amount = newAmount) }
        }
    }

    fun onCurrencyChange(newCurrency: String) {
        _formState.update { it.copy(currency = newCurrency) }
    }

    fun onBillingCycleChange(newCycle: BillingCycle) {
        _formState.update { it.copy(billingCycle = newCycle) }
    }

    fun onDateChange(newDate: Date) {
        _formState.update { it.copy(firstPaymentDate = newDate) }
    }

    // Harpidetza datu-basean gordetzeko funtzioa
    fun saveSubscription(onSuccess: () -> Unit) {
        val state = _formState.value
        if (state.name.isBlank() || state.amount.isBlank()) {
            // Balidazio sinplea: izena eta kopurua ezin dira hutsik egon
            return
        }

        val subscriptionToSave = Subscription(
            name = state.name,
            amount = state.amount.toDouble(),
            currency = state.currency,
            billingCycle = state.billingCycle,
            firstPaymentDate = state.firstPaymentDate
        )

        viewModelScope.launch {
            subscriptionDao.insert(subscriptionToSave)
            // onSUCCESS deitu nabigazioa kudeatzeko
            onSuccess()
        }
    }
}