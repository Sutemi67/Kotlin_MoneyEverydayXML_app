package com.example.moneyeverydayxml.di

import com.example.moneyeverydayxml.ui.ViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { ViewModel(get()) }
}