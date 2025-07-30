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

    sealed class ParseResult {
        data class Success(val amount: BigDecimal, val pattern: NotificationPattern) : ParseResult()
        data class NoAmount(val pattern: NotificationPattern) : ParseResult()
        object NoMatch : ParseResult()
        object NoPatterns : ParseResult()
    }

    suspend fun parseNotification(title: String, text: String): ParseResult {
        val fullText = "$title $text"
        val fullTextLower = fullText.lowercase()

        val patterns = patternDao.getAllPatterns().first()

        if (patterns.isEmpty()) {
            Log.d("CustomNotificationParser", "Нет пользовательских шаблонов")
            return ParseResult.NoPatterns
        }

        Log.d("CustomNotificationParser", "Проверяем уведомление: '$title' - '$text'")
        Log.d("CustomNotificationParser", "Доступно шаблонов: ${patterns.size}")

        for (pattern in patterns) {
            if (containsKeywords(fullTextLower, pattern.keywords)) {
                Log.d("CustomNotificationParser", "Найден подходящий шаблон: '${pattern.keywords}' (${if (pattern.isIncome) "доход" else "расход"})")

                val amount = extractAmountFromText(fullText)
                return if (amount != null) {
                    val finalAmount = if (pattern.isIncome) amount else amount.negate()
                    Log.d("CustomNotificationParser", "✓ Извлечена сумма: $finalAmount (исходная: $amount) по шаблону: '${pattern.keywords}'")
                    ParseResult.Success(finalAmount, pattern)
                } else {
                    Log.d("CustomNotificationParser", "✗ Сумма не найдена в тексте, соответствующем шаблону: '${pattern.keywords}'")
                    ParseResult.NoAmount(pattern)
                }
            }
        }

        Log.d("CustomNotificationParser", "✗ Уведомление не соответствует ни одному шаблону")
        return ParseResult.NoMatch
    }

    private fun containsKeywords(text: String, keywords: String): Boolean {
        val keywordList = keywords.split(",").map { it.trim().lowercase() }
        return keywordList.all { keyword -> text.contains(keyword) }
    }

    private fun extractAmountFromText(text: String): BigDecimal? {
        val amountPatterns = listOf(
            "\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*\\.\\d{2})\\s*₽\\b",
            "\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*\\.\\d{2})\\s*руб\\b",
            "\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*\\.\\d{2})\\s*RUB\\b",
            "\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*\\.\\d{2})\\s*\\$\\b",
            "\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*\\.\\d{2})\\s*USD\\b",
            "\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*\\.\\d{2})\\s*€\\b",
            "\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*\\.\\d{2})\\s*EUR\\b",
            "\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*₽\\b",
            "\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*руб\\b",
            "\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*RUB\\b",
            "\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*\\$\\b",
            "\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*USD\\b",
            "\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*€\\b",
            "\\b(\\d+[\\s,]*\\d*[\\s,]*\\d*)\\s*EUR\\b",
            "\\b(\\d+\\.\\d{2})\\b"
        )

        for ((index, pattern) in amountPatterns.withIndex()) {
            val regex = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE)
            val matcher = regex.matcher(text)
            if (matcher.find()) {
                val amountStr = matcher.group(1)?.replace("\\s".toRegex(), "")?.replace(",", "")
                if (amountStr != null) {
                    try {
                        return BigDecimal(amountStr)
                    } catch (e: NumberFormatException) {
                        Log.e("CustomNotificationParser", "Ошибка парсинга суммы '$amountStr': ${e.message}")
                        continue
                    }
                }
            }
        }
        return null
    }
} 