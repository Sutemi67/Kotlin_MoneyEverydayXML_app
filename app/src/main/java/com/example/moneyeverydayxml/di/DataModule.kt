package com.example.moneyeverydayxml.di

import com.example.moneyeverydayxml.data.Repository
import com.example.moneyeverydayxml.domain.RepositoryInterface
import org.koin.dsl.module

val dataModule = module {

    single<RepositoryInterface> { Repository(get()) }

}