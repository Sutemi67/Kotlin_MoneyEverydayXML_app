package com.example.moneyeverydayxml.history.domain.model

import java.math.BigDecimal

data class MainData(
    val dateOfClear: Long,
    val summaryAmount: BigDecimal
)