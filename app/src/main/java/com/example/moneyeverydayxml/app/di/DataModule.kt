package com.example.moneyeverydayxml.app.di

import androidx.room.Room
import com.example.moneyeverydayxml.core.data.Repository
import com.example.moneyeverydayxml.core.domain.RepositoryInterface
import com.example.moneyeverydayxml.history.data.Database
import com.example.moneyeverydayxml.history.data.NotificationPatternDao
import com.example.moneyeverydayxml.history.data.TransactionConverter
import com.example.moneyeverydayxml.notification.parser.CustomNotificationParser
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {

    single<RepositoryInterface> { Repository(get(), get()) }

    single {
        Room.databaseBuilder(androidContext(), Database::class.java, "database.db")
            .fallbackToDestructiveMigration(false)
            .build()
    }
    single { TransactionConverter() }
    single { get<Database>().notificationPatternDao() }
    single { CustomNotificationParser(get()) }
}
