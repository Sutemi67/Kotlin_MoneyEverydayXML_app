package com.example.moneyeverydayxml.data

import android.content.SharedPreferences
import com.example.moneyeverydayxml.domain.RepositoryInterface
import com.example.moneyeverydayxml.util.DAY_OF_CLEAR_PREF_KEY
import com.example.moneyeverydayxml.util.OPERATIONS_AMOUNTS_PREF_KEY
import com.example.moneyeverydayxml.util.OPERATION_DATES_SAVE_KEY
import com.example.moneyeverydayxml.util.SUMMARY_SAVE_KEY
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.math.BigDecimal
import java.math.RoundingMode

class Repository(
    private val preferences: SharedPreferences
) : RepositoryInterface {
    private var operationsDates = mutableListOf("", "", "", "", "")
    private var operationsCounts = mutableListOf("", "", "", "", "")

    override fun saveData(amount: String, date: String, summary: BigDecimal) {
        operationsDates.add(0, date)
        operationsCounts.add(0, amount)
        val jsonCounts = Gson().toJson(operationsCounts)
        val jsonDates = Gson().toJson(operationsDates)
        preferences.edit()
            .putString(OPERATIONS_AMOUNTS_PREF_KEY, jsonCounts)
            .apply()
        preferences.edit()
            .putString(OPERATION_DATES_SAVE_KEY, jsonDates)
            .apply()
        preferences.edit()
            .putString(SUMMARY_SAVE_KEY, summary.setScale(2, RoundingMode.DOWN).toString())
            .apply()
    }

    override fun getDatesList(): List<String> = operationsDates
    override fun getCountsList(): List<String> = operationsCounts
    override fun getClearDate(): Long = preferences.getLong(DAY_OF_CLEAR_PREF_KEY, 0L)
    override fun getSumFromMemory(): String = preferences.getString(SUMMARY_SAVE_KEY, "0") ?: "0"

    override fun loadData() {
        val jsonCounts =
            preferences.getString(OPERATIONS_AMOUNTS_PREF_KEY, null)
        val jsonDates =
            preferences.getString(OPERATION_DATES_SAVE_KEY, null)
        val itemType = object : TypeToken<MutableList<String>>() {}.type
        operationsCounts = if (jsonCounts != null) {
            Gson().fromJson(jsonCounts, itemType)
        } else {
            mutableListOf("", "", "", "", "")
        }
        operationsDates = if (jsonDates != null) {
            Gson().fromJson(jsonDates, itemType)
        } else {
            mutableListOf("", "", "", "", "")
        }
    }

    override fun saveClearDate(clearDate: Long) {
        preferences.edit()
            .putLong(DAY_OF_CLEAR_PREF_KEY, clearDate)
            .apply()
    }


}