package com.example.moneyeverydayxml.calculator.data

import android.content.SharedPreferences
import com.example.moneyeverydayxml.app.DAY_OF_CLEAR_PREF_KEY
import com.example.moneyeverydayxml.app.SUMMARY_SAVE_KEY
import com.example.moneyeverydayxml.calculator.domain.RepositoryInterface
import com.example.moneyeverydayxml.history.data.Database
import com.example.moneyeverydayxml.history.data.TransactionConverter
import com.example.moneyeverydayxml.history.domain.model.MainData
import com.example.moneyeverydayxml.history.domain.model.Transaction

class Repository(
    private val preferences: SharedPreferences,
    private val database: Database,
    private val converter: TransactionConverter
) : RepositoryInterface {

    override suspend fun saveTransaction(transaction: Transaction) {
        val entities = converter.mapToTransactionEntity(transaction)
        database.databaseDao().insertOperation(entities)
    }

    override suspend fun loadTransactions():List<Transaction> {
        val a = database.databaseDao().getTransactionsList()
        val d = converter.mapToTransactionList(a)
        return d
    }


    override fun saveMainData(mainFile: MainData) {
        preferences.edit()
            .putLong(DAY_OF_CLEAR_PREF_KEY, mainFile.dateOfClear)
            .putString(SUMMARY_SAVE_KEY, mainFile.summaryAmount.toString())
            .apply()
    }

    override fun loadMainData(): MainData {
        val dayOfClear = preferences.getLong(DAY_OF_CLEAR_PREF_KEY, 0L)
        val summary = preferences.getString(SUMMARY_SAVE_KEY, "0.00") ?: "0.00"
        return MainData(dayOfClear, summary.toBigDecimal())
    }


}