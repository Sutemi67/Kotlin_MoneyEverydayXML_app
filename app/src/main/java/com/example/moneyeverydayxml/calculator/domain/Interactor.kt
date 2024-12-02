package com.example.moneyeverydayxml.calculator.domain

import java.math.BigDecimal

class Interactor(
    private val repository: RepositoryInterface
) : InteractorInterface {
    override suspend fun saveTransaction(amount: String, date: String) {
        repository.saveTransaction(amount, date)
    }

    override fun saveMainData(clearDate: Long, summary: BigDecimal) =
        repository.saveMainData(clearDate, summary)

    override fun loadData() = repository.loadData()
    override fun getClearDate(): Long = repository.getClearDate()
    override fun getSumFromMemory(): String = repository.getSumFromMemory()

}