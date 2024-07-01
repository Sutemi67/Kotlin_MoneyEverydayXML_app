package com.example.moneyeverydayxml.history

class Savings {
    private var index: Int = 0
    private var operations: Array<Int> = arrayOf(5, 5, 5, 5, 5)

    fun saveOperation(input: Int) {
        operations[index] = input
        index++
    }
}


