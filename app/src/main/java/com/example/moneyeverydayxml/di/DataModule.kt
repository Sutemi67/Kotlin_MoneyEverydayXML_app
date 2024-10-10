package com.example.moneyeverydayxml.di

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.example.moneyeverydayxml.data.Repository
import com.example.moneyeverydayxml.domain.RepositoryInterface
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {

    single<RepositoryInterface> { Repository(get()) }
    single<SharedPreferences> { androidContext().getSharedPreferences("Repository", MODE_PRIVATE) }

}