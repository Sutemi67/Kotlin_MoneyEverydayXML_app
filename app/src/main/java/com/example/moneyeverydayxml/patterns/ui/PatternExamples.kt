package com.example.moneyeverydayxml.patterns.ui

import com.example.moneyeverydayxml.core.domain.model.NotificationPattern

object PatternExamples {
    
    val examples = listOf(
        // Сбербанк
        PatternExample(
            name = "Сбербанк - зачисление",
            keywords = "зачислен, сбербанк",
            isIncome = true
        ),
        PatternExample(
            name = "Сбербанк - списание",
            keywords = "списан, сбербанк",
            isIncome = false
        ),
        
        // Тинькофф
        PatternExample(
            name = "Тинькофф - пополнение",
            keywords = "пополнение, тинькофф",
            isIncome = true
        ),
        PatternExample(
            name = "Тинькофф - покупка",
            keywords = "покупка, тинькофф",
            isIncome = false
        ),
        
        // Альфа-Банк
        PatternExample(
            name = "Альфа-Банк - перевод",
            keywords = "перевод, альфа",
            isIncome = true
        ),
        
        // ВТБ
        PatternExample(
            name = "ВТБ - операция",
            keywords = "операция, втб",
            isIncome = false
        ),
        
        // Облигации и инвестиции
        PatternExample(
            name = "Выплата купона",
            keywords = "выплата, купон",
            isIncome = true
        ),
        PatternExample(
            name = "Дивиденды",
            keywords = "дивиденд",
            isIncome = true
        ),
        
        // Общие шаблоны
        PatternExample(
            name = "Любая сумма в рублях",
            keywords = "₽, руб",
            isIncome = true
        ),
        PatternExample(
            name = "Любая сумма в долларах",
            keywords = "$, доллар",
            isIncome = true
        ),
        PatternExample(
            name = "Любая сумма в евро",
            keywords = "€, евро",
            isIncome = true
        )
    )
    
    data class PatternExample(
        val name: String,
        val keywords: String,
        val isIncome: Boolean
    )
} 