package com.example.moneyeverydayxml.calculator.domain

import java.math.BigDecimal

interface RepositoryInterface {
    suspend fun saveTransaction(amount: String, date: String)
    fun saveMainData(clearDate: Long, summary: BigDecimal)
    fun loadData()
    fun getClearDate(): Long
    fun getSumFromMemory(): String
}
