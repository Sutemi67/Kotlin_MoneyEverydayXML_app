package com.example.moneyeverydayxml.ui

import android.content.SharedPreferences
import com.example.moneyeverydayxml.util.SAVINGS_CLASS_SAVE_KEY
import com.google.gson.Gson

class Savings {
    private var operationsDates = mutableListOf("", "", "", "", "")
    private var operationsCounts = mutableListOf("", "", "", "", "")
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