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

private const val TAG = "OROI_DEBUG"

class EditSubscriptionViewModel(
    private val subscriptionDao: SubscriptionDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _formState = MutableStateFlow(SubscriptionFormState())
    val formState = _formState.asStateFlow()

    // IDa ezin da null izan editatzeko pantailan
    private val editingSubscriptionId: Int

    init {
        // DEBUG: Ikusi ea ViewModel-a ID-a jasotzen ari den sortzean
        val receivedId = savedStateHandle.get<Int>("subscriptionId")
        Log.d(TAG, "[VM INIT] EditViewModel sortzen. Jasotako ID-a: $receivedId")

        editingSubscriptionId = checkNotNull(receivedId) { "subscriptionId ezinbestekoa da editatzeko." }

        Log.d(TAG, "[VM INIT] ID-a kargatuko da: $editingSubscriptionId")
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

    fun saveSubscription(onSuccess: () -> Unit) {
        val state = _formState.value
        // DEBUG: Ikusi zer gordetzen saiatzen ari garen
        Log.d(TAG, "[VM SAVE] Gorde sakatuta. Gordetzeko izena: '${state.name}'")
        if (state.name.isBlank() || state.amount.isBlank()) return

        val subscriptionToSave = Subscription(
            id = editingSubscriptionId,
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
        viewModelScope.launch {
            // ZUZENDUTA: 'amount' eremuak zenbaki bat behar du, ez testu huts bat.
            val sub = Subscription(id = editingSubscriptionId, name = "", amount = 0.0, currency = "", billingCycle = BillingCycle.MONTHLY, firstPaymentDate = Date())
            subscriptionDao.delete(sub)
            onSuccess()
        }
    }

    fun onNameChange(newName: String) {
        _formState.update { it.copy(name = newName) }
    }
    fun onAmountChange(newAmount: String) { if (newAmount.isEmpty() || newAmount.matches(Regex("^\\d*\\.?\\d*\$"))) { _formState.update { it.copy(amount = newAmount) } } }
    fun onBillingCycleChange(newCycle: BillingCycle) { _formState.update { it.copy(billingCycle = newCycle) } }
    fun onDateChange(newDate: Date) { _formState.update { it.copy(firstPaymentDate = newDate) } }
}