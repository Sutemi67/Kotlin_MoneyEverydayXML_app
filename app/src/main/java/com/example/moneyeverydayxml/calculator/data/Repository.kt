package com.example.moneyeverydayxml.calculator.data

import android.content.SharedPreferences
import com.example.moneyeverydayxml.app.DAY_OF_CLEAR_PREF_KEY
import com.example.moneyeverydayxml.app.SUMMARY_SAVE_KEY
import com.example.moneyeverydayxml.calculator.domain.RepositoryInterface
import com.example.moneyeverydayxml.history.data.Database
import com.example.moneyeverydayxml.history.data.TransactionConverter
import com.example.moneyeverydayxml.history.domain.model.Transaction
import java.math.BigDecimal

class Repository(
    private val preferences: SharedPreferences,
    private val database: Database,
    private val converter: TransactionConverter
) : RepositoryInterface {

    override suspend fun saveTransaction(amount: String, date: String) {
        val transaction = Transaction(date = date, count = amount.toString())
        val entities = converter.mapToTransactionEntity(transaction)
        database.databaseDao().insertOperation(entities)
    }

    override fun getClearDate(): Long = preferences.getLong(DAY_OF_CLEAR_PREF_KEY, 0L)
    override fun getSumFromMemory(): String =
        preferences.getString(SUMMARY_SAVE_KEY, "0.00") ?: "0.00"

    override fun loadData() {
        val a = database.databaseDao().getTransactionsList()
        val d = converter.mapToTransactionList(a)
    }

    override fun saveMainData(clearDate: Long, summary: BigDecimal) {
        preferences.edit()
            .putLong(DAY_OF_CLEAR_PREF_KEY, clearDate)
            .putString(SUMMARY_SAVE_KEY, summary.toString())
            .apply()
    }
}