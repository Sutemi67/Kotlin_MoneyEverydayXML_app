package com.example.moneyeverydayxml.di

import com.example.moneyeverydayxml.domain.Interactor
import com.example.moneyeverydayxml.domain.InteractorInterface
import org.koin.dsl.module

val domainModule = module {
    single<InteractorInterface> { Interactor(get()) }
}