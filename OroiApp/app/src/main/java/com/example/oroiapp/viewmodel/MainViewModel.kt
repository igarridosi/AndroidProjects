package com.example.oroiapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oroiapp.data.SubscriptionDao
import com.example.oroiapp.model.BillingCycle
import com.example.oroiapp.model.Subscription
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Interfazearen egoera definitzen du
data class MainUiState(
    val subscriptions: List<Subscription> = emptyList(),
    val totalMonthlyCost: Double = 0.0
)

class MainViewModel(private val subscriptionDao: SubscriptionDao) : ViewModel() {

    // StateFlow bat interfazearen egoera erakusteko
    val uiState: StateFlow<MainUiState> = subscriptionDao.getAllSubscriptions()
        .map { subs ->
            MainUiState(
                subscriptions = subs,
                totalMonthlyCost = calculateTotalMonthlyCost(subs)
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = MainUiState() // Hasierako egoera hutsa
        )

    // Harpidetza bat ezabatzeko funtzioa
    fun deleteSubscription(subscription: Subscription) {
        viewModelScope.launch {
            subscriptionDao.delete(subscription)
        }
    }

    // Hileko kostu totala kalkulatzeko logika
    private fun calculateTotalMonthlyCost(subscriptions: List<Subscription>): Double {
        return subscriptions.sumOf { sub ->
            when (sub.billingCycle) {
                BillingCycle.WEEKLY -> sub.amount * 4 // Hurbilketa
                BillingCycle.MONTHLY -> sub.amount
                BillingCycle.ANNUAL -> sub.amount / 12
            }
        }
    }
}