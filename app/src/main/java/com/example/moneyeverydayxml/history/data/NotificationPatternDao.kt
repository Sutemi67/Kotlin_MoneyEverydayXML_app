package com.example.moneyeverydayxml.history.data

import androidx.room.*
import com.example.moneyeverydayxml.core.domain.model.NotificationPattern
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationPatternDao {
    
    @Query("SELECT * FROM notification_patterns ORDER BY createdAt DESC")
    fun getAllPatterns(): Flow<List<NotificationPattern>>
    

    
    @Insert
    suspend fun insertPattern(pattern: NotificationPattern): Long
    
    @Update
    suspend fun updatePattern(pattern: NotificationPattern)
    
    @Delete
    suspend fun deletePattern(pattern: NotificationPattern)
    
    @Query("DELETE FROM notification_patterns WHERE id = :patternId")
    suspend fun deletePatternById(patternId: Long)
    

} 