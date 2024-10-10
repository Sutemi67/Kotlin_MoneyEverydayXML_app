package com.example.moneyeverydayxml.data

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.example.moneyeverydayxml.domain.RepositoryInterface
import com.example.moneyeverydayxml.util.DAY_OF_CLEAR_PREF_KEY
import com.example.moneyeverydayxml.util.MONTH_SUMMARY_PREF_KEY
import com.example.moneyeverydayxml.util.OPERATION_DATES_SAVE_KEY
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Repository(
    context: Context,
) : RepositoryInterface {
    private var operationsDates = mutableListOf("", "", "", "", "")
    private var operationsCounts = mutableListOf("", "", "", "", "")

    private var datePrefs: SharedPreferences =
        context.getSharedPreferences(OPERATION_DATES_SAVE_KEY, MODE_PRIVATE)
    private var countPrefs: SharedPreferences =
        context.getSharedPreferences(MONTH_SUMMARY_PREF_KEY, MODE_PRIVATE)
    private var clearPrefs: SharedPreferences =
        context.getSharedPreferences(DAY_OF_CLEAR_PREF_KEY, MODE_PRIVATE)

    override fun saveData(amount: String, date: String, clearDate: Long) {
        operationsDates.add(0, date)
        operationsCounts.add(0, amount)
        val jsonCounts = Gson().toJson(operationsCounts)
        val jsonDates = Gson().toJson(operationsDates)
        countPrefs.edit()
            .putString(MONTH_SUMMARY_PREF_KEY, jsonCounts)
            .apply()
        datePrefs.edit()
            .putString(OPERATION_DATES_SAVE_KEY, jsonDates)
            .apply()
        clearPrefs.edit()
            .putLong(DAY_OF_CLEAR_PREF_KEY, clearDate)
            .apply()
    }

    override fun getData(): Long {
        val jsonCounts =
            countPrefs.getString(MONTH_SUMMARY_PREF_KEY, null)
        val jsonDates =
            datePrefs.getString(OPERATION_DATES_SAVE_KEY, null)
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
        val clearDate: Long = clearPrefs.getLong(DAY_OF_CLEAR_PREF_KEY, 0L)
        return clearDate
    }

}