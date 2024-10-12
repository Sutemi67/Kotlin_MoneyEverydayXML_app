package com.example.moneyeverydayxml.domain

interface RepositoryInterface {
    fun saveData(amount: String, date: String, summary:Int)
    fun saveClearDate(clearDate: Long)
    fun loadData()
    fun getClearDate(): Long
    fun getDatesList(): List<String>
    fun getSumFromMemory(): String
    fun getCountsList(): List<String>
}
