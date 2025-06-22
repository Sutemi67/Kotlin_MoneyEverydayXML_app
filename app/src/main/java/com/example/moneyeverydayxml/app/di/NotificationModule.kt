package com.example.moneyeverydayxml.app.di

import com.example.moneyeverydayxml.notification.NotificationPermissionManager
import com.example.moneyeverydayxml.notification.parser.NotificationParser
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val notificationModule = module {
    single { NotificationPermissionManager(androidContext()) }
    single { NotificationParser() }
} 