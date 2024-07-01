package com.example.moneyeverydayxml.history

import android.content.SharedPreferences
import com.example.moneyeverydayxml.SAVINGS_CLASS_SAVE_KEY
import com.example.moneyeverydayxml.lastInput
import com.google.gson.Gson

class Savings {
    var operationsDates = mutableListOf(5, 5, 5, 5, 5)

    fun saveOperation() {
        operationsDates.add(0, lastInput)
    }
}

fun saveSavingsBySharedPref(savings: Savings, sharedPreferences: SharedPreferences) {
    val json = Gson().toJson(savings)
    sharedPreferences.edit()
        .putString(SAVINGS_CLASS_SAVE_KEY, json)
        .apply()
}

fun readSavingsBySharedPref(sharedPreferences: SharedPreferences): Savings {
    val json = sharedPreferences.getString(SAVINGS_CLASS_SAVE_KEY, null)
    return Gson().fromJson(json, Savings::class.java)
}