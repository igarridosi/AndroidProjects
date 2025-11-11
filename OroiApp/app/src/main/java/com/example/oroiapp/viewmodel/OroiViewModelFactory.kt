package com.example.oroiapp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.oroiapp.data.SubscriptionDao

object OroiViewModelFactory : ViewModelProvider.Factory {

    lateinit var dao: SubscriptionDao

    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
        val savedStateHandle = extras.createSavedStateHandle()

        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(dao) as T
            }
            // ORDENA: Lehenengo 'application', gero 'dao'
            modelClass.isAssignableFrom(AddEditViewModel::class.java) -> {
                AddEditViewModel(application, dao) as T
            }
            // ORDENA: Lehenengo 'application', gero 'dao', gero 'savedStateHandle'
            modelClass.isAssignableFrom(EditSubscriptionViewModel::class.java) -> {
                // Fix: Swapped 'dao' and 'application' to match the constructor's expected order
                EditSubscriptionViewModel(dao, application, savedStateHandle) as T
            }
            else -> {
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}