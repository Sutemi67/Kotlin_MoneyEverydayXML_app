package com.example.moneyeverydayxml.core.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity
data class MainData(
    @PrimaryKey
    val id: Int = 0,
    val dateOfClear: Long,
    val summaryAmount: BigDecimal
) 