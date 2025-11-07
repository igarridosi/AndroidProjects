package com.example.oroiapp.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oroiapp.data.SubscriptionDao
import com.example.oroiapp.model.BillingCycle
import com.example.oroiapp.model.Subscription
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import android.util.Log
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val TAG = "OROI_DEBUG"

class EditSubscriptionViewModel(
    private val subscriptionDao: SubscriptionDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _formState = MutableStateFlow(SubscriptionFormState())
    val formState = _formState.asStateFlow()

    // IDa ezin da null izan editatzeko pantailan
    private val _navigationChannel = Channel<Unit>()
    val navigationEvent = _navigationChannel.receiveAsFlow()

    private val editingSubscriptionId: Int = checkNotNull(savedStateHandle["subscriptionId"])

    init {
        loadSubscriptionData()
    }

    private fun loadSubscriptionData() {
        viewModelScope.launch {
            // Orain datuak BEHIN BAKARRIK kargatzen ditugu,
            // eta ez dugu datu-basea behatzen geratzen.
            val subscription = subscriptionDao.getSubscriptionById(editingSubscriptionId)

            Log.d(TAG, "[VM LOAD] Datu-basetik kargatutako harpidetza: ${subscription?.name}")
            if (subscription != null) {
                _formState.value = SubscriptionFormState( // '.value' erabili '.update' beharrean, Flow-a ez behatzeko
                    name = subscription.name,
                    amount = subscription.amount.toString(),
                    currency = subscription.currency,
                    billingCycle = subscription.billingCycle,
                    firstPaymentDate = subscription.firstPaymentDate
                )
            }
        }
    }

    fun saveSubscription() {
        viewModelScope.launch {
            val state = _formState.value
            if (state.name.isBlank() || state.amount.isBlank()) return@launch

            val subscriptionToSave = Subscription(
                id = editingSubscriptionId,
                name = state.name,
                amount = state.amount.toDouble(),
                currency = state.currency,
                billingCycle = state.billingCycle,
                firstPaymentDate = state.firstPaymentDate
            )
            subscriptionDao.insert(subscriptionToSave)

            _navigationChannel.send(Unit)
        }
    }

    fun deleteSubscription() {
        viewModelScope.launch {
            val sub = Subscription(id = editingSubscriptionId, name = "", amount = 0.0, currency = "", billingCycle = BillingCycle.MONTHLY, firstPaymentDate = Date())
            subscriptionDao.delete(sub)

            _navigationChannel.send(Unit)
        }
    }
    fun onNameChange(newName: String) {
        _formState.update { it.copy(name = newName) }
    }
    fun onAmountChange(newAmount: String) { if (newAmount.isEmpty() || newAmount.matches(Regex("^\\d*\\.?\\d*\$"))) { _formState.update { it.copy(amount = newAmount) } } }
    fun onBillingCycleChange(newCycle: BillingCycle) { _formState.update { it.copy(billingCycle = newCycle) } }
    fun onDateChange(newDate: Date) { _formState.update { it.copy(firstPaymentDate = newDate) } }
}