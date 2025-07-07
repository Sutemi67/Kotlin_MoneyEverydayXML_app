package com.example.moneyeverydayxml.core.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification_patterns")
data class NotificationPattern(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val keywords: String, // Ключевые слова для поиска (через запятую)
    val isActive: Boolean = true, // Активен ли шаблон
    val isIncome: Boolean = true, // Доход (true) или расход (false)
    val createdAt: Long = System.currentTimeMillis()
) 