package com.example.moneyeverydayxml.calculator.domain

import com.example.moneyeverydayxml.history.domain.model.MainData
import com.example.moneyeverydayxml.history.domain.model.Transaction

interface RepositoryInterface {
    suspend fun saveTransaction(transaction: Transaction)
    suspend fun loadTransactions():List<Transaction>
    suspend fun clearAllTransactions()

    fun saveMainData(mainFile: MainData)
    fun loadMainData(): MainData

    fun addTransactionAndUpdateSummary(transaction: Transaction)
}
