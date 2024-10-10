package com.example.moneyeverydayxml.domain

interface RepositoryInterface {
    fun saveData(amount: String, date: String)
    fun saveClearDate(clearDate: Long)
    fun loadData()
    fun getClearDate(): Long
}
