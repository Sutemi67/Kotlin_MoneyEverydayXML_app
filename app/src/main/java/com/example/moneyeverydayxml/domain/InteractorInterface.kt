package com.example.moneyeverydayxml.domain

interface InteractorInterface {
    fun saveData(amount: String, date: String)
    fun saveClearDate(clearDate: Long)
    fun loadData()
    fun getClearDate():Long

}