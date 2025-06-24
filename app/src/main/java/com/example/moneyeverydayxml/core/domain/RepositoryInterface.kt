package com.example.moneyeverydayxml.core.domain

import com.example.moneyeverydayxml.core.domain.model.MainData
import com.example.moneyeverydayxml.core.domain.model.Transaction

interface RepositoryInterface {
    suspend fun saveTransaction(transaction: Transaction)
    suspend fun loadTransactions():List<Transaction>
    suspend fun clearAllTransactions()
    suspend fun deleteTransaction(transaction: Transaction)
    suspend fun updateTransaction(transaction: Transaction)

    suspend fun saveMainData(mainData: MainData)
    suspend fun loadMainData(): MainData

    suspend fun addTransactionAndUpdateSummary(transaction: Transaction)
} 