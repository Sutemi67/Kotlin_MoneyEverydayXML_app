package com.example.moneyeverydayxml.notification.parser

import android.util.Log
import java.math.BigDecimal
import java.util.regex.Pattern

class NotificationParser {

    companion object {
        private val AMOUNT_PATTERNS = listOf(
            Pattern.compile("(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*₽"), // 1 000 ₽
            Pattern.compile("(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*руб"), // 1 000 руб
            Pattern.compile("(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*рубл"), // 1 000 рублей
            Pattern.compile("(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*RUB"), // 1 000 RUB
            Pattern.compile("(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*р"), // 1 000 р

            Pattern.compile("(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*\\$"), // 1 000 $
            Pattern.compile("(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*USD"), // 1 000 USD
            Pattern.compile("(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*долл"), // 1 000 долларов

            Pattern.compile("(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*€"), // 1 000 €
            Pattern.compile("(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*EUR"), // 1 000 EUR
            Pattern.compile("(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*евро"), // 1 000 евро

            Pattern.compile("\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\b")
        )

        private val FINANCIAL_KEYWORDS = setOf(
            // Доходы
            "зачислен", "зачисление", "пополнение", "входящий", "получен", "начислен",
            "income", "received", "credited", "deposit", "transfer received",
            "поступил", "поступление", "начисление", "пополнен", "зачислено",

            // Расходы
            "списан", "списание", "платеж", "перевод", "оплата", "покупка", "расход",
            "expense", "payment", "purchase", "transfer sent", "withdrawal",
            "списано", "платеж", "оплачено", "потрачено", "израсходовано",

            // Общие финансовые термины
            "баланс", "счет", "карта", "банк", "банковский", "финанс",
            "balance", "account", "card", "bank", "financial", "transaction",
            "операция", "транзакция", "деньги", "средства", "валюта"
        )

        private val INCOME_KEYWORDS = setOf(
            "зачислен", "зачисление", "пополнение", "входящий", "получен", "начислен",
            "income", "received", "credited", "deposit", "transfer received",
            "поступил", "поступление", "начисление", "пополнен", "зачислено"
        )

        private val EXPENSE_KEYWORDS = setOf(
            "списан", "списание", "платеж", "перевод", "оплата", "покупка", "расход",
            "expense", "payment", "purchase", "transfer sent", "withdrawal",
            "списано", "платеж", "оплачено", "потрачено", "израсходовано"
        )
    }

    fun isFinancialTransaction(title: String, text: String): Boolean {
        val fullText = "$title $text".lowercase()
        val hasAmount = AMOUNT_PATTERNS.any { it.matcher(fullText).find() }
        val hasFinancialKeywords = FINANCIAL_KEYWORDS.any { fullText.contains(it) }
        return hasAmount && hasFinancialKeywords
    }

    fun extractAmount(title: String, text: String): BigDecimal? {
        val fullText = "$title $text".lowercase()

        for (pattern in AMOUNT_PATTERNS) {
            val matcher = pattern.matcher(fullText)
            if (matcher.find()) {
                val amountStr = matcher.group(1)?.replace("\\s".toRegex(), "")?.replace(",", "")
                if (amountStr != null) {
                    try {
                        val amount = BigDecimal(amountStr)

                        val isExpense = isExpenseTransaction(title, text)
                        return if (isExpense) amount.negate() else amount

                    } catch (e: NumberFormatException) {
                        Log.e("erorr", "${e.message}")
                        continue
                    }
                }
            }
        }

        return null
    }

    private fun isExpenseTransaction(title: String, text: String): Boolean {
        val fullText = "$title $text".lowercase()

        val incomeCount = INCOME_KEYWORDS.count { fullText.contains(it) }
        val expenseCount = EXPENSE_KEYWORDS.count { fullText.contains(it) }

        return expenseCount > incomeCount
    }
} 