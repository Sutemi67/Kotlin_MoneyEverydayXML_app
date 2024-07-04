package com.example.moneyeverydayxml.history

import android.content.SharedPreferences
import android.icu.util.Calendar
import com.example.moneyeverydayxml.SAVINGS_CLASS_SAVE_KEY
import com.example.moneyeverydayxml.lastInput
import com.google.gson.Gson
import java.util.Date

class Savings {
    var operationsDates = mutableListOf("", "", "", "", "")
    var operationsCounts = mutableListOf("", "", "", "", "")

    fun saveOperation() {
        operationsDates.add(0, Date(Calendar.getInstance().timeInMillis).toString())
        operationsCounts.add(0, lastInput.toString())
    }
}

fun saveSavingsBySharedPref(savings: Savings, sharedPreferences: SharedPreferences) {
    val json = Gson().toJson(savings)
    sharedPreferences.edit()
        .putString(SAVINGS_CLASS_SAVE_KEY, json)
        .apply()
}

fun readSavingsBySharedPref(sharedPreferences: SharedPreferences): Savings {
    val json = sharedPreferences.getString(SAVINGS_CLASS_SAVE_KEY, null) ?: return Savings()
    return Gson().fromJson(json, Savings::class.java)
}