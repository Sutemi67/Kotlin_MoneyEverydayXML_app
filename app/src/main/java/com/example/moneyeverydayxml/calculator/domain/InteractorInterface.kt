package com.example.moneyeverydayxml.calculator.domain

import com.example.moneyeverydayxml.history.domain.model.MainData
import com.example.moneyeverydayxml.history.domain.model.Transaction

interface InteractorInterface {
    suspend fun saveTransaction(transaction: Transaction)
    suspend fun loadTransactions():List<Transaction>

    fun saveMainData(mainFile: MainData)
    fun loadMainData(): MainData
}