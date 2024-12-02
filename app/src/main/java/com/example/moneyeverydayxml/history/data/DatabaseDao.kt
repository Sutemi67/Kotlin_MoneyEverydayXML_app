package com.example.moneyeverydayxml.history.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DatabaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOperation(transaction: TransactionEntity): Long

    @Query("select*from transactions order by time desc")
    suspend fun getTransactionsList(): List<TransactionEntity>

    @Query("DELETE FROM transactions WHERE id = (SELECT id FROM transactions ORDER BY id ASC LIMIT 1)")
    suspend fun deleteOldestTransaction()

    @Query("SELECT COUNT(*) FROM transactions")
    suspend fun getTransactionCount(): Int

}