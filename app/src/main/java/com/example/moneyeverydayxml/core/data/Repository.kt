package com.example.moneyeverydayxml.core.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.moneyeverydayxml.app.DAY_OF_CLEAR_PREF_KEY
import com.example.moneyeverydayxml.app.SUMMARY_SAVE_KEY
import com.example.moneyeverydayxml.core.domain.RepositoryInterface
import com.example.moneyeverydayxml.core.domain.model.MainData
import com.example.moneyeverydayxml.core.domain.model.Transaction
import com.example.moneyeverydayxml.history.data.Database
import com.example.moneyeverydayxml.history.data.TransactionConverter
import java.math.BigDecimal

class Repository(
    private val preferences: SharedPreferences,
    private val database: Database,
    private val converter: TransactionConverter
) : RepositoryInterface {

    override suspend fun saveTransaction(transaction: Transaction) {
        val currentCount = database.databaseDao().getTransactionCount()
        if (currentCount >= 50) {
            database.databaseDao().deleteOldestTransaction()
        }
        val entities = converter.mapToTransactionEntity(transaction)
        database.databaseDao().insertOperation(entities)
    }

    override suspend fun loadTransactions(): List<Transaction> {
        val a = database.databaseDao().getTransactionsList()
        val d = converter.mapToTransactionList(a)
        return d
    }

    override suspend fun clearAllTransactions() {
        database.databaseDao().clearAllTransactions()
    }

    override fun saveMainData(mainFile: MainData) {
        preferences.edit {
            putLong(DAY_OF_CLEAR_PREF_KEY, mainFile.dateOfClear)
            putString(SUMMARY_SAVE_KEY, mainFile.summaryAmount.toString())
        }
    }

    override fun loadMainData(): MainData {
        val dayOfClear = preferences.getLong(DAY_OF_CLEAR_PREF_KEY, 0L)
        val summary = preferences.getString(SUMMARY_SAVE_KEY, "0.00") ?: "0.00"
        return MainData(dayOfClear, summary.toBigDecimal())
    }

    override suspend fun addTransactionAndUpdateSummary(transaction: Transaction) {
        saveTransaction(transaction)
        val currentData = loadMainData()
        val transactionAmount = try {
            BigDecimal(transaction.count)
        } catch (e: NumberFormatException) {
            BigDecimal.ZERO
        }
        val newSummary = currentData.summaryAmount + transactionAmount
        saveMainData(
            MainData(
                dateOfClear = currentData.dateOfClear,
                summaryAmount = newSummary
            )
        )
    }
} 