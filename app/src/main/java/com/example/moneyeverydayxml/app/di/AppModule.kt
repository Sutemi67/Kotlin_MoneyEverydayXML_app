package com.example.moneyeverydayxml.app.di

import com.example.moneyeverydayxml.calculator.ui.CalculatorViewModel
import com.example.moneyeverydayxml.history.ui.HistoryViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel<CalculatorViewModel> { CalculatorViewModel(get()) }
    viewModel<HistoryViewModel> { HistoryViewModel(get()) }
}