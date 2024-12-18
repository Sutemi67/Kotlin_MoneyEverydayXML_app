package com.example.moneyeverydayxml.history.data

import com.example.moneyeverydayxml.history.domain.model.Transaction

class TransactionConverter {
    fun mapToTransactionList(list: List<TransactionEntity>): List<Transaction> {
        return list.map {
            Transaction(
                it.time,
                it.date,
                it.count
            )
        }
    }

    fun mapToTransactionEntity(item: Transaction): TransactionEntity {
        return TransactionEntity(
            time = item.time,
            date = item.date,
            count = item.count
        )
    }
}