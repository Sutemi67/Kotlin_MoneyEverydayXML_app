package com.example.moneyeverydayxml.app.di

import com.example.moneyeverydayxml.app.MainViewModel
import com.example.moneyeverydayxml.calculator.ui.CalculatorViewModel
import com.example.moneyeverydayxml.history.ui.HistoryViewModel
import com.example.moneyeverydayxml.patterns.ui.PatternsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MainViewModel() }
    viewModel { CalculatorViewModel(get()) }
    viewModel { HistoryViewModel(get()) }
    viewModel { PatternsViewModel(get()) }
} 