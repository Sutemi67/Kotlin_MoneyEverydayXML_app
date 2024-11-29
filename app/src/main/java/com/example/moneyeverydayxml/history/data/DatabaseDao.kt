package com.example.moneyeverydayxml.history.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DatabaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOperation(transaction: TransactionEntity): Long

    @Query("select*from transactions")
    fun getTransactionsList(): List<TransactionEntity>
}