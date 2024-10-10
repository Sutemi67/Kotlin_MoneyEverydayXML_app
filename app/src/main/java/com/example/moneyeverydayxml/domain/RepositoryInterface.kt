package com.example.moneyeverydayxml.domain

interface RepositoryInterface {
    fun saveData(amount: String, date: String, clearDate: Long)
    fun getData():Long

}
