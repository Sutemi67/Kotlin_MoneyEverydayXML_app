package com.example.moneyeverydayxml.history.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    @ColumnInfo(name = "time") val time: Long,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "count") val count: String
)