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

    override suspend fun saveTransaction(amount: String, date: String, summary: BigDecimal) {
        val transaction = Transaction(date = date, count = summary.toString())
        val entities = converter.mapToEntities(listOf(transaction))
        val saveInDatabase = database.databaseDao().insertOperation(entities.first())
    }

    override fun getClearDate(): Long = preferences.getLong(DAY_OF_CLEAR_PREF_KEY, 0L)
    override fun getSumFromMemory(): String =
        preferences.getString(SUMMARY_SAVE_KEY, "0.00") ?: "0.00"

    override fun loadData() {
        val a = database.databaseDao().getTransactionsList()
        val d = converter.mapToTransactions(a)
    }

    override fun saveClearDate(clearDate: Long) {
        preferences.edit()
            .putLong(DAY_OF_CLEAR_PREF_KEY, clearDate)
            .putLong(SUMMARY_SAVE_KEY)
            .apply()
    }


}