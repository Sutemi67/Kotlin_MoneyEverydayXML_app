package com.example.moneyeverydayxml.domain

interface InteractorInterface {
    fun saveData(amount: String, date: String, summary:Int)
    fun saveClearDate(clearDate: Long)
    fun loadData()
    fun getClearDate(): Long
    fun getDatesList(): List<String>
    fun getDatesFromMemory():String
    fun getSumFromMemory(): String
}