package com.example.moneyeverydayxml.notification.parser

import android.util.Log
import com.example.moneyeverydayxml.core.domain.model.NotificationPattern
import com.example.moneyeverydayxml.history.data.NotificationPatternDao
import kotlinx.coroutines.flow.first
import java.math.BigDecimal
import java.util.regex.Pattern

class CustomNotificationParser(
    private val patternDao: NotificationPatternDao
) {

    suspend fun isFinancialTransaction(title: String, text: String, packName: String): Boolean {
        val fullText = "$title $text".lowercase()
        
        // Получаем все пользовательские шаблоны
        val patterns = patternDao.getAllPatterns().first()
        
        if (patterns.isEmpty()) {
            Log.d("CustomNotificationParser", "Нет пользовательских шаблонов")
            return false
        }

        // Проверяем каждый шаблон по ключевым словам
        for (pattern in patterns) {
            if (containsKeywords(fullText, pattern.keywords)) {
                Log.d("CustomNotificationParser", "Найдено совпадение с шаблоном: ${pattern.keywords}")
                return true
            }
        }
        
        return false
    }

    suspend fun extractAmount(title: String, text: String): BigDecimal? {
        val fullText = "$title $text"
        
        // Получаем все пользовательские шаблоны
        val patterns = patternDao.getAllPatterns().first()
        
        if (patterns.isEmpty()) {
            return null
        }

        // Ищем сумму по пользовательским шаблонам
        for (pattern in patterns) {
            if (containsKeywords(fullText.lowercase(), pattern.keywords)) {
                // Извлекаем сумму из текста после ключевых слов
                val amount = extractAmountFromText(fullText)
                if (amount != null) {
                    Log.d("CustomNotificationParser", "Найдена сумма: $amount по шаблону: ${pattern.keywords}")
                    return if (pattern.isIncome) amount else amount.negate()
                }
            }
        }

        return null
    }

    private fun containsKeywords(text: String, keywords: String): Boolean {
        val keywordList = keywords.split(",").map { it.trim().lowercase() }
        return keywordList.all { keyword -> text.contains(keyword) }
    }

    private fun extractAmountFromText(text: String): BigDecimal? {
        Log.e("CustomNotificationParser", "=== НАЧАЛО ПОИСКА СУММЫ ===")
        Log.e("CustomNotificationParser", "Текст для поиска: $text")
        
        // Паттерны для поиска сумм с валютой (в порядке приоритета)
        val amountPatterns = listOf(
            // Сначала ищем суммы с валютой (более точные)
            "\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*\\.\\d{2})\\s*₽\\b",
            "\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*\\.\\d{2})\\s*руб\\b",
            "\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*\\.\\d{2})\\s*RUB\\b",
            "\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*\\.\\d{2})\\s*\\$\\b",
            "\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*\\.\\d{2})\\s*USD\\b",
            "\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*\\.\\d{2})\\s*€\\b",
            "\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*\\.\\d{2})\\s*EUR\\b",
            
            // Затем суммы с валютой без десятичных
            "\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*₽\\b",
            "\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*руб\\b",
            "\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*RUB\\b",
            "\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*\\$\\b",
            "\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*USD\\b",
            "\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*€\\b",
            "\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*EUR\\b",
            
            // В последнюю очередь простые десятичные числа
            "\\b(\\d+\\.\\d{2})\\b"
        )
        
        for ((index, pattern) in amountPatterns.withIndex()) {
            val regex = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE)
            val matcher = regex.matcher(text)
            if (matcher.find()) {
                val amountStr = matcher.group(1)?.replace("\\s".toRegex(), "")?.replace(",", "")
                Log.e("CustomNotificationParser", "ПАТТЕРН $index ($pattern) НАШЕЛ: $amountStr")
                if (amountStr != null) {
                    try {
                        val amount = BigDecimal(amountStr)
                        Log.e("CustomNotificationParser", "УСПЕШНО ИЗВЛЕЧЕНА СУММА: $amount")
                        return amount
                    } catch (e: NumberFormatException) {
                        Log.e("CustomNotificationParser", "Ошибка парсинга суммы: ${e.message}")
                        continue
                    }
                }
            }
        }
        
        Log.e("CustomNotificationParser", "СУММА НЕ НАЙДЕНА")
        return null
    }
} 