package com.example.moneyeverydayxml.app.di

import com.example.moneyeverydayxml.calculator.domain.Interactor
import com.example.moneyeverydayxml.calculator.domain.InteractorInterface
import org.koin.dsl.module

val domainModule = module {
    single<InteractorInterface> { Interactor(get()) }
}