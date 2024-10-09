package com.example.moneyeverydayxml.data

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.example.moneyeverydayxml.domain.RepositoryInterface
import com.example.moneyeverydayxml.ui.Savings
import com.example.moneyeverydayxml.util.DAY_OF_CLEAR_PREF_KEY
import com.example.moneyeverydayxml.util.MONTH_SUMMARY_PREF_KEY
import com.example.moneyeverydayxml.util.SAVINGS_CLASS_SAVE_KEY
import com.google.gson.Gson

class Repository(
    private val context: Context,
) : RepositoryInterface {
     private val operationsDates = mutableListOf("", "", "", "", "")
     private var operationsCounts = mutableListOf("", "", "", "", "")

    private var datePrefs: SharedPreferences = context.getSharedPreferences(DAY_OF_CLEAR_PREF_KEY, MODE_PRIVATE)
    private var countPrefs: SharedPreferences = context.getSharedPreferences(MONTH_SUMMARY_PREF_KEY, MODE_PRIVATE)

    override fun saveData(date:String, count:String, summ:Int) {
        datePrefs.edit().putString()
    }

    override fun getData() {
        TODO("Not yet implemented")
    }

    fun saveSavingsBySharedPref(savings: MutableList<String>, sharedPreferences: SharedPreferences) {
        val json = Gson().toJson(savings)
        sharedPreferences.edit()
            .putString(SAVINGS_CLASS_SAVE_KEY, json)
            .apply()
    }

    fun readSavingsBySharedPref(sharedPreferences: SharedPreferences): Savings {
        val json = sharedPreferences.getString(SAVINGS_CLASS_SAVE_KEY, null) ?: return Savings()
        return Gson().fromJson(json, Savings::class.java)
    }

}