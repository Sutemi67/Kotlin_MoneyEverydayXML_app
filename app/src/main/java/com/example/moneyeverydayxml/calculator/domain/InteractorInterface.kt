package com.example.moneyeverydayxml.calculator.domain

import java.math.BigDecimal

interface InteractorInterface {
    fun saveData(amount: String, date: String, summary: BigDecimal)
    fun saveClearDate(clearDate: Long)
    fun loadData()
    fun getClearDate(): Long
    fun getDatesList(): List<String>
    fun getCountsList(): List<String>
    fun getSumFromMemory(): String
}