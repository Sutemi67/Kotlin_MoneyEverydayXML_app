package com.example.moneyeverydayxml.notification.parser

import android.util.Log
import java.math.BigDecimal
import java.util.regex.Pattern

class NotificationParser {

    companion object {
        private val AMOUNT_PATTERNS = listOf(
            // Десятичные числа с валютой (высший приоритет)
            Pattern.compile("\\b(\\d+\\.\\d{2})\\s*₽\\b"), // 163.23 ₽
            Pattern.compile("\\b(\\d+\\.\\d{2})\\s*руб\\b"), // 163.23 руб
            Pattern.compile("\\b(\\d+\\.\\d{2})\\s*рубл\\b"), // 163.23 рублей
            Pattern.compile("\\b(\\d+\\.\\d{2})\\s*RUB\\b"), // 163.23 RUB
            Pattern.compile("\\b(\\d+\\.\\d{2})\\s*р\\b"), // 163.23 р

            Pattern.compile("\\b(\\d+\\.\\d{2})\\s*\\$\\b"), // 163.23 $
            Pattern.compile("\\b(\\d+\\.\\d{2})\\s*USD\\b"), // 163.23 USD
            Pattern.compile("\\b(\\d+\\.\\d{2})\\s*долл\\b"), // 163.23 долларов

            Pattern.compile("\\b(\\d+\\.\\d{2})\\s*€\\b"), // 163.23 €
            Pattern.compile("\\b(\\d+\\.\\d{2})\\s*EUR\\b"), // 163.23 EUR
            Pattern.compile("\\b(\\d+\\.\\d{2})\\s*евро\\b"), // 163.23 евро

            // Целые числа с валютой
            Pattern.compile("\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*₽\\b"), // 1 000 ₽
            Pattern.compile("\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*руб\\b"), // 1 000 руб
            Pattern.compile("\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*рубл\\b"), // 1 000 рублей
            Pattern.compile("\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*RUB\\b"), // 1 000 RUB
            Pattern.compile("\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*р\\b"), // 1 000 р

            Pattern.compile("\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*\\$\\b"), // 1 000 $
            Pattern.compile("\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*USD\\b"), // 1 000 USD
            Pattern.compile("\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*долл\\b"), // 1 000 долларов

            Pattern.compile("\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*€\\b"), // 1 000 €
            Pattern.compile("\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*EUR\\b"), // 1 000 EUR
            Pattern.compile("\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*евро\\b"), // 1 000 евро

            // Общий паттерн для чисел (используется в последнюю очередь)
            Pattern.compile("\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\b")
        )

        private val EXCLUDE_KEYWORDS = setOf(
            // Звонки и телефонные уведомления
            "звонок", "вызов", "call", "incoming call", "outgoing call", "missed call",
            "входящий звонок", "исходящий звонок", "пропущенный звонок",
            "набирает", "dialing", "calling", "phone", "телефон",

            // SMS и сообщения
            "sms", "сообщение", "message", "mms", "текст",

            // Время и дата
            "минут", "часов", "дней", "недель", "месяцев", "лет",
            "minutes", "hours", "days", "weeks", "months", "years",

            // Процентные ставки и другие нефинансовые числа
            "процент", "процентов", "percent", "проц", "%",

            // Номера телефонов (могут содержать цифры)
            "номер", "number", "тел", "tel", "phone number",

            // Время разговора
            "длительность", "duration", "время разговора", "call duration",

            // Батарея и другие системные уведомления
            "батарея", "battery", "заряд", "charge", "разряд", "discharge",

            // Погода и другие сервисы
            "погода", "weather", "температура", "temperature", "градус", "degree"
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
            "операция", "транзакция", "деньги", "средства", "валюта",

            // Облигации и инвестиции
            "облигация", "облигации", "bond", "bonds", "погашение", "выплата",
            "купон", "coupon", "дивиденд", "dividend", "инвестиция", "investment"
        )
        private val INCOME_KEYWORDS = setOf(
            "зачислен",
            "зачисление",
            "пополнение",
            "входящий",
            "получен",
            "начислен",
            "income",
            "received",
            "credited",
            "deposit",
            "transfer received",
            "поступил",
            "поступление",
            "начисление",
            "пополнен",
            "зачислено",
            "погашение",
            "выплатил",
            "выплата",
            "выплачено"
        )
        private val EXPENSE_KEYWORDS = setOf(
            "списан", "списание", "платеж", "перевод", "оплата", "покупка", "расход",
            "expense", "payment", "purchase", "transfer sent", "withdrawal",
            "списано", "платеж", "оплачено", "потрачено", "израсходовано"
        )
        private val EXCLUDE_PACKAGES = setOf(
            "org.telegram", "com.whatsapp", "com.vk"
        )
    }

    fun isFinancialTransaction(title: String, text: String, packName: String): Boolean {
        val fullText = "$title $text".lowercase()
//        val hasAmount = AMOUNT_PATTERNS.any { it.matcher(fullText).find() }
//        val hasFinancialKeywords = FINANCIAL_KEYWORDS.any { fullText.contains(it) }
        when {
            EXCLUDE_KEYWORDS.any { fullText.contains(it) } -> {
                Log.d(
                    "NotificationParser",
                    "Уведомление содержит исключающие ключевые слова: $fullText"
                )
                return false
            }

//            EXCLUDE_PACKAGES.any { packName.contains(it) } -> {
//                Log.d(
//                    "NotificationParser",
//                    "Уведомление содержит исключающие приложения: $packName"
//                )
//                return false
//            }
        }
        // Ищем суммы с валютой (более точные паттерны)
        val hasAmountWithCurrency = AMOUNT_PATTERNS.take(AMOUNT_PATTERNS.size - 1).any {
            it.matcher(fullText).find()
        }

        // Ищем числа с десятичными знаками (суммы)
        val hasDecimalAmount = Pattern.compile("\\b(\\d+\\.\\d{2})\\b").matcher(fullText).find()

        val hasAmount = hasAmountWithCurrency || hasDecimalAmount
        val hasFinancialKeywords = FINANCIAL_KEYWORDS.any { fullText.contains(it) }

        // Если есть сумма с валютой или десятичная сумма, считаем это финансовой транзакцией
        if (hasAmount) {
            Log.d("NotificationParser", "Найдена финансовая транзакция: $fullText")
            return true
        }

        // Если есть финансовые ключевые слова, но нет суммы, логируем для отладки
        if (hasFinancialKeywords) {
            Log.d("NotificationParser", "Найдены финансовые ключевые слова без суммы: $fullText")
        }

        return false
    }

    fun extractAmount(title: String, text: String): BigDecimal? {
        val fullText = "$title $text"

        // Ищем суммы по всем паттернам в порядке приоритета
        for (i in 0 until AMOUNT_PATTERNS.size - 1) { // Исключаем последний общий паттерн
            val pattern = AMOUNT_PATTERNS[i]
            val matcher = pattern.matcher(fullText)
            
            // Ищем первое совпадение для каждого паттерна
            if (matcher.find()) {
                val amountStr = matcher.group(1)?.replace("\\s".toRegex(), "")?.replace(",", "")
                if (amountStr != null) {
                    try {
                        val amount = BigDecimal(amountStr)
                        val isExpense = isExpenseTransaction(title, text)
                        Log.d("NotificationParser", "Найдена сумма: $amount (паттерн $i)")
                        return if (isExpense) amount.negate() else amount
                    } catch (e: NumberFormatException) {
                        Log.e("NotificationParser", "Ошибка парсинга суммы: ${e.message}")
                        continue
                    }
                }
            }
        }

        // В последнюю очередь используем общий паттерн для чисел
        val generalPattern = AMOUNT_PATTERNS.last()
        val generalMatcher = generalPattern.matcher(fullText)
        if (generalMatcher.find()) {
            val amountStr = generalMatcher.group(1)?.replace("\\s".toRegex(), "")?.replace(",", "")
            if (amountStr != null) {
                try {
                    val amount = BigDecimal(amountStr)
                    val isExpense = isExpenseTransaction(title, text)
                    Log.d("NotificationParser", "Найдена общая сумма: $amount")
                    return if (isExpense) amount.negate() else amount
                } catch (e: NumberFormatException) {
                    Log.e("NotificationParser", "Ошибка парсинга общей суммы: ${e.message}")
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