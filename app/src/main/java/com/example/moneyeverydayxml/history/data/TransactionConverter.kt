package com.example.moneyeverydayxml.history.data

import com.example.moneyeverydayxml.core.domain.model.Transaction

class TransactionConverter {
    fun mapToTransactionList(list: List<TransactionEntity>): List<Transaction> {
        return list.map {
            Transaction(
                it.id,
                it.time,
                it.date,
                it.count,
                it.description
            )
        }
    }

    fun mapToTransactionEntity(item: Transaction): TransactionEntity {
        return TransactionEntity(
            id = item.id,
            time = item.time,
            date = item.date,
            count = item.count,
            description = item.description
        )
    }
}