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
    val totalMonthlyCost: Double = 0.0,
    val totalAnnualCost: Double = 0.0,
    val totalDailyCost: Double = 0.0
)

private data class AllCosts(
    val monthly: Double,
    val annual: Double,
    val daily: Double
)

class MainViewModel(private val subscriptionDao: SubscriptionDao) : ViewModel() {

    // StateFlow bat interfazearen egoera erakusteko
    val uiState: StateFlow<MainUiState> = subscriptionDao.getAllSubscriptions()
        .map { subs ->
            val allCosts = calculateAllCosts(subs)
            MainUiState(
                subscriptions = subs,
                totalMonthlyCost = allCosts.monthly,
                totalAnnualCost = allCosts.annual,
                totalDailyCost = allCosts.daily
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

    // 3. Funtzio nagusi bat kostu guztiak kalkulatzeko
    private fun calculateAllCosts(subscriptions: List<Subscription>): AllCosts {
        val monthlyCost = subscriptions.sumOf { sub ->
            when (sub.billingCycle) {
                BillingCycle.WEEKLY -> sub.amount * 4 // Hurbilketa
                BillingCycle.MONTHLY -> sub.amount
                BillingCycle.ANNUAL -> sub.amount / 12
            }
        }
        val annualCost = monthlyCost * 12
        val dailyCost = monthlyCost / 30 // Hurbilketa (30 eguneko hilabetea)

        return AllCosts(monthly = monthlyCost, annual = annualCost, daily = dailyCost)
    }
}