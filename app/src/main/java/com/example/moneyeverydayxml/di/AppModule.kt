package com.example.moneyeverydayxml.di

import com.example.moneyeverydayxml.ui.CalculatorViewModel
import com.example.moneyeverydayxml.ui.HistoryViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel<CalculatorViewModel> { CalculatorViewModel(get()) }
    viewModel<HistoryViewModel> { HistoryViewModel(get()) }
}