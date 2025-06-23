package com.example.moneyeverydayxml.history.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.example.moneyeverydayxml.core.domain.model.MainData

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

    @Query("DELETE FROM transactions")
    suspend fun clearAllTransactions()

    @Upsert
    suspend fun upsertMainData(mainData: MainData)

    @Query("SELECT * FROM MainData WHERE id = 0")
    suspend fun getMainData(): MainData?
}