package com.example.moneyeverydayxml.core.domain

import com.example.moneyeverydayxml.core.domain.model.MainData
import com.example.moneyeverydayxml.core.domain.model.Transaction

interface RepositoryInterface {
    suspend fun saveTransaction(transaction: Transaction)
    suspend fun loadTransactions():List<Transaction>
    suspend fun clearAllTransactions()

    fun saveMainData(mainFile: MainData)
    fun loadMainData(): MainData

    suspend fun addTransactionAndUpdateSummary(transaction: Transaction)
} 