package com.example.moneyeverydayxml.util

import android.app.Application
import com.example.moneyeverydayxml.di.appModule
import com.example.moneyeverydayxml.di.dataModule
import com.example.moneyeverydayxml.di.domainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class AppMain : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AppMain)
            modules(dataModule, domainModule, appModule)
        }
    }
}