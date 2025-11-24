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
import com.example.oroiapp.data.ThemeSetting
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

// Interfazearen egoera definitzen du
data class MainUiState(
    val subscriptions: List<Subscription> = emptyList(),
    val totalMonthlyCost: Double = 0.0,
    val totalAnnualCost: Double = 0.0,
    val totalDailyCost: Double = 0.0,
    val username: String = "",
    val showUsernameDialog: Boolean = false,
    val currentTheme: ThemeSetting = ThemeSetting.SYSTEM
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

    // Sortu MutableStateFlow bat uneko gaia gordetzeko ViewModel-ean.
    // Hasierako balioa SharedPreferences-etik irakurtzen dugu.
    private val _currentTheme = MutableStateFlow(userPrefs.getThemeSetting())

    // StateFlow bat interfazearen egoera erakusteko
    private val _username = MutableStateFlow(userPrefs.getUsername())
    private val _showUsernameDialog = MutableStateFlow(userPrefs.isFirstLaunch())
    private val _dialogUsernameInput = MutableStateFlow("")
    val dialogUsernameInput: StateFlow<String> = _dialogUsernameInput.asStateFlow()

    val uiState: StateFlow<MainUiState> = combine(
        subscriptionDao.getAllSubscriptions(),
        _username,
        _showUsernameDialog,
        _currentTheme
    ) { subs, name, showDialog, theme ->
        val allCosts = calculateAllCosts(subs)
        MainUiState(
            subscriptions = subs,
            totalMonthlyCost = allCosts.monthly,
            totalAnnualCost = allCosts.annual,
            totalDailyCost = allCosts.daily,
            username = name,
            showUsernameDialog = showDialog,
            currentTheme = theme
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = MainUiState()
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

    fun changeTheme(newTheme: ThemeSetting) {
        userPrefs.saveThemeSetting(newTheme) // Gorde SharedPreferences-en
        _currentTheme.value = newTheme       // Eguneratu ViewModel-eko egoera
    }

    fun onDialogUsernameChange(name: String) {
        _dialogUsernameInput.value = name
    }

    fun onUsernameSave() {
        val name = _dialogUsernameInput.value.trim()
        if (name.isNotBlank()) {
            userPrefs.saveUsername(name)
            _username.value = name
            _showUsernameDialog.value = false
        }
    }
}