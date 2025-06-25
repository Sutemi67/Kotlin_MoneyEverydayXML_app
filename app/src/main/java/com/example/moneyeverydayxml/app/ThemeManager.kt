package com.example.moneyeverydayxml.app

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

class ThemeManager(context: Context) {
    
    companion object {
        private const val PREF_NAME = "theme_preferences"
        private const val KEY_THEME_MODE = "theme_mode"
    }
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    
    fun getCurrentThemeMode(): Int {
        return sharedPreferences.getInt(KEY_THEME_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
    
    fun setThemeMode(themeMode: Int) {
        sharedPreferences.edit { putInt(KEY_THEME_MODE, themeMode) }
        AppCompatDelegate.setDefaultNightMode(themeMode)
    }
    
    fun toggleTheme() {
        val currentMode = getCurrentThemeMode()
        val newMode = when (currentMode) {
            AppCompatDelegate.MODE_NIGHT_NO -> AppCompatDelegate.MODE_NIGHT_YES
            AppCompatDelegate.MODE_NIGHT_YES -> AppCompatDelegate.MODE_NIGHT_NO
            else -> AppCompatDelegate.MODE_NIGHT_NO
        }
        setThemeMode(newMode)
    }
    
    fun isDarkTheme(): Boolean {
        return getCurrentThemeMode() == AppCompatDelegate.MODE_NIGHT_YES
    }
} 