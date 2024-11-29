package com.example.moneyeverydayxml.calculator.domain

import java.math.BigDecimal

interface RepositoryInterface {
    suspend fun saveTransaction(amount: String, date: String, summary: BigDecimal)
    fun saveClearDate(clearDate: Long)
    fun loadData()
    fun getClearDate(): Long
    fun getSumFromMemory(): String
}
