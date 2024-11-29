package com.example.moneyeverydayxml.history.data

import com.example.moneyeverydayxml.history.domain.model.Transaction

class TransactionConverter {
    fun mapToTransactions(list: List<TransactionEntity>): List<Transaction> {
        return list.map {
            Transaction(
                it.id,
                it.date,
                it.count
            )
        }
    }

    fun mapToEntities(list: List<Transaction>): List<TransactionEntity> {
        return list.map {
            TransactionEntity(
                id = it.id ?: 0,
                it.date,
                it.count
            )
        }
    }
}