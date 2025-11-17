package com.example.oroiapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oroiapp.data.SubscriptionDao
import com.example.oroiapp.data.UserPreferencesRepository
import com.example.oroiapp.model.BillingCycle
import com.example.oroiapp.model.Subscription
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Interfazearen egoera definitzen du
data class MainUiState(
    val subscriptions: List<Subscription> = emptyList(),
    val totalMonthlyCost: Double = 0.0,
    val totalAnnualCost: Double = 0.0,
    val totalDailyCost: Double = 0.0,
    val username: String = "",
    val showUsernameDialog: Boolean = false
)

private data class AllCosts(
    val monthly: Double,
    val annual: Double,
    val daily: Double
)

class MainViewModel(
    private val subscriptionDao: SubscriptionDao,
    private val userPrefs: UserPreferencesRepository
) : ViewModel() {

    // StateFlow bat interfazearen egoera erakusteko
    private val _username = MutableStateFlow(userPrefs.getUsername())
    private val _showUsernameDialog = MutableStateFlow(userPrefs.isFirstLaunch())
    private val _dialogUsernameInput = MutableStateFlow("")
    val dialogUsernameInput: StateFlow<String> = _dialogUsernameInput.asStateFlow()

    val uiState: StateFlow<MainUiState> = combine(
        subscriptionDao.getAllSubscriptions(), // Flow 1: Tu fuente de datos principal
        _username,                             // Flow 2: El nombre de usuario
        _showUsernameDialog                    // Flow 3: El flag para mostrar el diálogo
    ) { subs, name, showDialog ->
        // Tu lógica de cálculo se mantiene intacta
        val allCosts = calculateAllCosts(subs)
        // Creamos el estado final combinando los datos de los 3 flows
        MainUiState(
            subscriptions = subs,
            totalMonthlyCost = allCosts.monthly,
            totalAnnualCost = allCosts.annual,
            totalDailyCost = allCosts.daily,
            username = name,
            showUsernameDialog = showDialog
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = MainUiState() // El valor inicial sigue siendo el mismo
    )

    // Funtzio nagusi bat kostu guztiak kalkulatzeko
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

    fun onDialogUsernameChange(name: String) {
        _dialogUsernameInput.value = name
    }

    fun onUsernameSave() {
        val name = _dialogUsernameInput.value.trim()
        if (name.isNotBlank()) {
            // Guardamos el nombre de forma persistente
            userPrefs.saveUsername(name)
            // Actualizamos los flows de estado para que la UI reaccione
            _username.value = name
            _showUsernameDialog.value = false
        }
    }
}