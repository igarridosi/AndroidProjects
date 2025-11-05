package com.example.oroiapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.oroiapp.data.SubscriptionDao

class OroiViewModelFactory(private val dao: SubscriptionDao) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Egiaztatu eskatutako ViewModel-a gure proiektukoa den
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            // MainViewModel eskatzen badu, sortu eta itzuli, dao-a pasatuz
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(dao) as T
        }
        if (modelClass.isAssignableFrom(AddEditViewModel::class.java)) {
            // AddEditViewModel eskatzen badu, sortu eta itzuli
            @Suppress("UNCHECKED_CAST")
            return AddEditViewModel(dao) as T
        }

        // Eskatutako ViewModel-a ezagutzen ez badu, errore bat bota
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}