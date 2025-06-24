package com.example.moneyeverydayxml.history.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.moneyeverydayxml.core.domain.model.MainData
import java.math.BigDecimal

@Database(
    entities = [TransactionEntity::class, MainData::class],
    version = 6
)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {
    abstract fun databaseDao(): DatabaseDao
}

class Converters {
    @TypeConverter
    fun fromString(value: String?): BigDecimal? {
        return value?.let { BigDecimal(it) }
    }

    @TypeConverter
    fun bigDecimalToString(bigDecimal: BigDecimal?): String? {
        return bigDecimal?.toPlainString()
    }
}