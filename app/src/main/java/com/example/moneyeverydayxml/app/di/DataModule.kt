package com.example.moneyeverydayxml.app.di

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.room.Room
import com.example.moneyeverydayxml.calculator.data.Repository
import com.example.moneyeverydayxml.calculator.domain.RepositoryInterface
import com.example.moneyeverydayxml.history.data.Database
import com.example.moneyeverydayxml.history.data.TransactionConverter
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {

    single<RepositoryInterface> { Repository(get(), get(), get()) }
    single<SharedPreferences> { androidContext().getSharedPreferences("Repository", MODE_PRIVATE) }

    single {
        Room.databaseBuilder(androidContext(), Database::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .build()
    }
    single { TransactionConverter() }
}
