// En /data/UserPreferencesRepository.kt
package com.example.oroiapp.data

import android.content.Context
import android.content.SharedPreferences

class UserPreferencesRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isFirstLaunch(): Boolean {
        return prefs.getBoolean(IS_FIRST_LAUNCH_KEY, true)
    }

    fun getUsername(): String {
        return prefs.getString(USERNAME_KEY, "") ?: ""
    }

    fun saveUsername(name: String) {
        prefs.edit()
            .putString(USERNAME_KEY, name)
            .putBoolean(IS_FIRST_LAUNCH_KEY, false)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "OroiUserPrefs"
        private const val USERNAME_KEY = "username"
        private const val IS_FIRST_LAUNCH_KEY = "is_first_launch"
    }
}