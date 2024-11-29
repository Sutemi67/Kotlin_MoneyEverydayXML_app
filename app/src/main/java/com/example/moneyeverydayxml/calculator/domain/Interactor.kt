package com.example.moneyeverydayxml.calculator.domain

import java.math.BigDecimal

class Interactor(
    private val repository: RepositoryInterface
) : InteractorInterface {
    override fun saveData(amount: String, date: String, summary: BigDecimal) {
        repository.saveTransaction(amount, date, summary)
    }

    override fun saveClearDate(clearDate: Long) = repository.saveClearDate(clearDate)
    override fun loadData() = repository.loadData()
    override fun getClearDate(): Long = repository.getClearDate()
    override fun getDatesList(): List<String> = repository.getDatesList()
    override fun getCountsList(): List<String> = repository.getCountsList()
    override fun getSumFromMemory(): String = repository.getSumFromMemory()

}