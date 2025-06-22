package com.example.moneyeverydayxml.notification.parser

import java.math.BigDecimal
import java.util.regex.Pattern

class NotificationParser {

    companion object {
        // Регулярные выражения для поиска сумм в разных форматах
        private val AMOUNT_PATTERNS = listOf(
            // Основные паттерны для рублей
            Pattern.compile("(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*₽"), // 1 000 ₽
            Pattern.compile("(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*руб"), // 1 000 руб
            Pattern.compile("(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*рубл"), // 1 000 рублей
            Pattern.compile("(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*RUB"), // 1 000 RUB
            Pattern.compile("(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*р"), // 1 000 р

            // Паттерны для долларов
            Pattern.compile("(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*\\$"), // 1 000 $
            Pattern.compile("(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*USD"), // 1 000 USD
            Pattern.compile("(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*долл"), // 1 000 долларов

            // Паттерны для евро
            Pattern.compile("(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*€"), // 1 000 €
            Pattern.compile("(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*EUR"), // 1 000 EUR
            Pattern.compile("(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*евро"), // 1 000 евро

            // Простые числовые паттерны (если валюта не указана, считаем рубли)
            Pattern.compile("\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\b")
        )

        // Ключевые слова для определения финансовых транзакций
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

        // Ключевые слова для определения типа транзакции
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

    /**
     * Проверяет, содержит ли уведомление информацию о финансовой транзакции
     */
    fun isFinancialTransaction(title: String, text: String): Boolean {
        val fullText = "$title $text".lowercase()

        // Проверяем наличие суммы
        val hasAmount = AMOUNT_PATTERNS.any { it.matcher(fullText).find() }

        // Проверяем наличие финансовых ключевых слов
        val hasFinancialKeywords = FINANCIAL_KEYWORDS.any { fullText.contains(it) }

        // Уведомление считается финансовым, если содержит сумму И финансовые ключевые слова
        return hasAmount && hasFinancialKeywords
    }

    /**
     * Извлекает сумму из текста уведомления
     */
    fun extractAmount(title: String, text: String): BigDecimal? {
        val fullText = "$title $text".lowercase()

        for (pattern in AMOUNT_PATTERNS) {
            val matcher = pattern.matcher(fullText)
            if (matcher.find()) {
                val amountStr = matcher.group(1)?.replace("\\s".toRegex(), "")?.replace(",", "")
                if (amountStr != null) {
                    try {
                        val amount = BigDecimal(amountStr)

                        // Определяем знак суммы на основе контекста
                        val isExpense = isExpenseTransaction(title, text)
                        return if (isExpense) amount.negate() else amount

                    } catch (e: NumberFormatException) {
                        continue
                    }
                }
            }
        }

        return null
    }

    /**
     * Извлекает описание транзакции
     */
    fun extractDescription(title: String, text: String): String? {
        // Убираем сумму из текста и возвращаем оставшуюся часть
        val fullText = "$title $text"
        var cleanText = fullText

        // Удаляем суммы из текста
        for (pattern in AMOUNT_PATTERNS) {
            cleanText = pattern.matcher(cleanText).replaceAll("")
        }

        // Удаляем лишние пробелы и символы
        cleanText = cleanText.trim().replace("\\s+".toRegex(), " ")

        return if (cleanText.isNotEmpty()) cleanText else null
    }

    /**
     * Определяет, является ли транзакция расходом
     */
    private fun isExpenseTransaction(title: String, text: String): Boolean {
        val fullText = "$title $text".lowercase()

        val incomeCount = INCOME_KEYWORDS.count { fullText.contains(it) }
        val expenseCount = EXPENSE_KEYWORDS.count { fullText.contains(it) }

        // Если больше ключевых слов расходов - считаем расходом
        return expenseCount > incomeCount
    }
} 