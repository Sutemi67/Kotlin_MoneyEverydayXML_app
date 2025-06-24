package com.example.moneyeverydayxml.core.domain.model

data class Transaction(
    val id: Long?,
    val time: Long,
    val date: String,
    val count: String,
    val description: String
) 