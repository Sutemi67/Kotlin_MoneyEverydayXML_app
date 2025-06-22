package com.example.moneyeverydayxml.calculator.domain

import com.example.moneyeverydayxml.history.domain.model.MainData
import com.example.moneyeverydayxml.history.domain.model.Transaction

class Interactor(
    private val repository: RepositoryInterface
) : InteractorInterface {
    override suspend fun saveTransaction(transaction: Transaction) {
        repository.saveTransaction(transaction)
    }

    override suspend fun loadTransactions(): List<Transaction> {
        return repository.loadTransactions()
    }

    override suspend fun clearAllTransactions() {
        repository.clearAllTransactions()
    }

    override fun saveMainData(mainData: MainData) =
        repository.saveMainData(mainData)

    override fun loadMainData(): MainData {
        return repository.loadMainData()
    }

    override fun addTransactionAndUpdateSummary(transaction: Transaction) {
        repository.addTransactionAndUpdateSummary(transaction)
    }
}