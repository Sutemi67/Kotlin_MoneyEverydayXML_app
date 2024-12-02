package com.example.moneyeverydayxml.history.data

import com.example.moneyeverydayxml.history.domain.model.Transaction

class TransactionConverter {
    fun mapToTransactionList(list: List<TransactionEntity>): List<Transaction> {
        return list.map {
            Transaction(
                it.id,
                it.date,
                it.count
            )
        }
    }

    fun mapToTransactionEntity(item: Transaction): TransactionEntity {
        return TransactionEntity(
            id = item.id ?: 0,
            date = item.date,
            count = item.count
        )
    }
}