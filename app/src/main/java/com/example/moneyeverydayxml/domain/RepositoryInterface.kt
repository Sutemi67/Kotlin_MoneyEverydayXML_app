package com.example.moneyeverydayxml.domain

import java.math.BigDecimal

interface RepositoryInterface {
    fun saveData(amount: String, date: String, summary: BigDecimal)
    fun saveClearDate(clearDate: Long)
    fun loadData()
    fun getClearDate(): Long
    fun getDatesList(): List<String>
    fun getSumFromMemory(): String
    fun getCountsList(): List<String>
}
