package com.example.moneyeverydayxml.app

import android.app.Application
import com.example.moneyeverydayxml.app.di.appModule
import com.example.moneyeverydayxml.app.di.dataModule
import com.example.moneyeverydayxml.app.di.domainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(dataModule, domainModule, appModule)
        }
    }
}