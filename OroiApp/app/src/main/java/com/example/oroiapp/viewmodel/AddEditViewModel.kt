package com.example.oroiapp.viewmodel

import androidx.lifecycle.SavedStateHandle
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

class AddEditViewModel(
    private val subscriptionDao: SubscriptionDao,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _formState = MutableStateFlow(SubscriptionFormState())
    val formState = _formState.asStateFlow()

    private val _isEditing = MutableStateFlow(false)
    val isEditing = _isEditing.asStateFlow()

    private var editingSubscriptionId: Int? = null

    // ORAIN INIT ERABIL DEZAKEGU, ViewModel-a beti berria delako
    init {
        val subscriptionId = savedStateHandle.get<String>("subscriptionId")?.toIntOrNull()
        if (subscriptionId != null) {
            _isEditing.value = true
            editingSubscriptionId = subscriptionId
            loadSubscriptionData(subscriptionId)
        }
        // `else` kasuan ez da ezer egin behar, egoera lehenetsia hutsik dagoelako.
    }

    private fun loadSubscriptionData(id: Int) {
        viewModelScope.launch {
            val subscription = subscriptionDao.getSubscriptionById(id)
            if (subscription != null) {
                _formState.update {
                    it.copy(
                        name = subscription.name,
                        amount = subscription.amount.toString(),
                        currency = subscription.currency,
                        billingCycle = subscription.billingCycle,
                        firstPaymentDate = subscription.firstPaymentDate
                    )
                }
            }
        }
    }

    fun saveSubscription(onSuccess: () -> Unit) {
        // ZUZENDUTA: Bere barne-egoera erabiltzen du, ez parametroak
        val state = _formState.value
        if (state.name.isBlank() || state.amount.isBlank()) return

        val subscriptionToSave = Subscription(
            id = 0,
            name = state.name,
            amount = state.amount.toDouble(),
            currency = state.currency,
            billingCycle = state.billingCycle,
            firstPaymentDate = state.firstPaymentDate
        )
        viewModelScope.launch {
            subscriptionDao.insert(subscriptionToSave)
            onSuccess()
        }
    }

    fun deleteSubscription(onSuccess: () -> Unit) {
        editingSubscriptionId?.let { id ->
            viewModelScope.launch {
                val sub = Subscription(id = id, name = "", amount = 0.0, currency = "", billingCycle = BillingCycle.MONTHLY, firstPaymentDate = Date())
                subscriptionDao.delete(sub)
                onSuccess()
            }
        }
    }

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
}