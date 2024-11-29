package com.example.moneyeverydayxml.history.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TransactionEntity::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun databaseDao(): DatabaseDao
}