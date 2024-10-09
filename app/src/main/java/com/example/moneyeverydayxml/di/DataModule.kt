package com.example.moneyeverydayxml.di

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.example.moneyeverydayxml.data.Repository
import com.example.moneyeverydayxml.domain.RepositoryInterface
import com.example.moneyeverydayxml.util.DAY_OF_CLEAR_PREF_KEY
import com.example.moneyeverydayxml.util.MONTH_SUMMARY_PREF_KEY
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {

    single<RepositoryInterface> { Repository(get(), get()) }

    single<SharedPreferences> {
        androidContext().getSharedPreferences(DAY_OF_CLEAR_PREF_KEY, MODE_PRIVATE)
    }
    single<SharedPreferences> {
        androidContext().getSharedPreferences(MONTH_SUMMARY_PREF_KEY, MODE_PRIVATE)
    }


}