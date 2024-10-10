package com.example.moneyeverydayxml.domain

interface InteractorInterface {
    fun saveData(amount: String, date: String, clearDate: Long)
    fun getData():Long
}