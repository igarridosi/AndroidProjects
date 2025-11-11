package com.example.oroiapp.viewmodel

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.savedstate.SavedStateRegistryOwner
import com.example.oroiapp.data.SubscriptionDao

// Factory-a objektu gisa definituko dugu, erabilera errazteko
object OroiViewModelFactory : ViewModelProvider.Factory {

    // 'lateinit' erabiliko dugu DAO-a geroago ezartzeko
    lateinit var dao: SubscriptionDao

    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras // Mekanismo modernoa
    ): T {
        // Sistemak emandako 'extras'-etatik lortzen dugu 'SavedStateHandle' zuzena
        val savedStateHandle = extras.createSavedStateHandle()

        // Egiaztatu zein ViewModel sortu behar den
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(dao) as T
            }
            modelClass.isAssignableFrom(AddEditViewModel::class.java) -> {
                AddEditViewModel(dao) as T
            }
            modelClass.isAssignableFrom(EditSubscriptionViewModel::class.java) -> {
                EditSubscriptionViewModel(dao, savedStateHandle) as T
            }
            else -> {
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}