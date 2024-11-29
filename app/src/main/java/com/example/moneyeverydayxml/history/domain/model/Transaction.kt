package com.example.moneyeverydayxml.history.domain.model

import java.math.BigDecimal

data class Transaction(
    val date : Long,
    val count: BigDecimal
)
