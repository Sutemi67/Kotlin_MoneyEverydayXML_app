package com.example.moneyeverydayxml.app.di

import com.example.moneyeverydayxml.notification.NotificationPermissionManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val notificationModule = module {
    single { NotificationPermissionManager(androidContext()) }
} 