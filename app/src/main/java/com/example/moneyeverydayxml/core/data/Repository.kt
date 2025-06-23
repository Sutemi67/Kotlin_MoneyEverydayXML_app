package com.example.moneyeverydayxml.core.data

import androidx.room.withTransaction
import com.example.moneyeverydayxml.core.domain.RepositoryInterface
import com.example.moneyeverydayxml.core.domain.model.MainData
import com.example.moneyeverydayxml.core.domain.model.Transaction
import com.example.moneyeverydayxml.history.data.Database
import com.example.moneyeverydayxml.history.data.TransactionConverter
import java.math.BigDecimal

class Repository(
    private val database: Database,
    private val converter: TransactionConverter
) : RepositoryInterface {

    private val dao = database.databaseDao()

    override suspend fun saveTransaction(transaction: Transaction) {
        val currentCount = dao.getTransactionCount()
        if (currentCount >= 50) {
            dao.deleteOldestTransaction()
        }
        val entity = converter.mapToTransactionEntity(transaction)
        dao.insertOperation(entity)
    }

    override suspend fun loadTransactions(): List<Transaction> {
        val entities = dao.getTransactionsList()
        return converter.mapToTransactionList(entities)
    }

    override suspend fun clearAllTransactions() {
        dao.clearAllTransactions()
    }

    override suspend fun saveMainData(mainData: MainData) {
        dao.upsertMainData(mainData)
    }

    override suspend fun loadMainData(): MainData {
        return dao.getMainData() ?: MainData(dateOfClear = 0L, summaryAmount = BigDecimal.ZERO)
    }

    override suspend fun addTransactionAndUpdateSummary(transaction: Transaction) {
        database.withTransaction {
            val currentData = loadMainData()
            val transactionAmount = try {
                BigDecimal(transaction.count)
            } catch (e: NumberFormatException) {
                BigDecimal.ZERO
            }
            val newSummary = currentData.summaryAmount.add(transactionAmount)
            val newMainData = currentData.copy(summaryAmount = newSummary)

            saveMainData(newMainData)
            saveTransaction(transaction)
        }
    }
} 