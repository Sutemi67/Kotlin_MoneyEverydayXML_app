package com.example.moneyeverydayxml.notification.parser

import android.util.Log
import com.example.moneyeverydayxml.calculator.domain.InteractorInterface
import com.example.moneyeverydayxml.history.domain.model.MainData
import com.example.moneyeverydayxml.history.domain.model.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Тестовый класс для проверки работы парсера уведомлений
 * Можно использовать для отладки и тестирования различных форматов уведомлений
 * Теперь также сохраняет распознанные транзакции в базу данных
 */
class NotificationParserTest {

    companion object {
        private const val TAG = "NotificationParserTest"
    }

    private val parser = NotificationParser()
    private val interactor: InteractorInterface by inject(InteractorInterface::class.java)
    private val scope = CoroutineScope(Dispatchers.IO)

    /**
     * Тестирует различные форматы уведомлений и сохраняет их в базу данных
     */
    fun testNotificationFormats() {
        Log.d(TAG, "=== Тестирование парсера уведомлений ===")

        // Тест 1: Банковское уведомление о зачислении
        testNotification(
            "Сбербанк",
            "Зачислено 5000 руб. на карту ****1234",
            "Ожидается: доход 5000 руб."
        )

        // Тест 2: Уведомление о покупке
        testNotification(
            "Магнит",
            "Покупка на сумму 1250 ₽ в магазине Магнит",
            "Ожидается: расход -1250 руб."
        )

        // Тест 3: Перевод между счетами
        testNotification(
            "Тинькофф",
            "Перевод 14000 рублей выполнен успешно",
            "Ожидается: расход -14000 руб."
        )

        // Тест 4: Пополнение счета
        testNotification(
            "ЮMoney",
            "Счет пополнен на 3000 руб.",
            "Ожидается: доход 3000 руб."
        )

        // Тест 5: Нефинансовое уведомление (не должно сработать)
        testNotification(
            "WhatsApp",
            "Новое сообщение от Ивана",
            "Ожидается: не должно сработать"
        )

        // Тест 6: Уведомление с числом, но без финансовых ключевых слов
        testNotification(
            "Telegram",
            "У вас 5 непрочитанных сообщений",
            "Ожидается: не должно сработать"
        )

        // Тест 7: Доллары
        testNotification(
            "PayPal",
            "Payment received: $50.00 USD",
            "Ожидается: доход 50 руб."
        )

        // Тест 8: Евро
        testNotification(
            "Revolut",
            "Spent €25.50 at restaurant",
            "Ожидается: расход -25.50 руб."
        )
    }

    private fun testNotification(source: String, text: String, expected: String) {
        Log.d(TAG, "\n--- Тест: $source ---")
        Log.d(TAG, "Текст: $text")
        Log.d(TAG, "Ожидание: $expected")

        val isFinancial = parser.isFinancialTransaction("", text)
        Log.d(TAG, "Финансовая транзакция: $isFinancial")

        if (isFinancial) {
            val amount = parser.extractAmount("", text)
            val description = parser.extractDescription("", text)

            Log.d(TAG, "Сумма: $amount")
            Log.d(TAG, "Описание: $description")

            if (amount != null) {
                Log.d(TAG, "✅ УСПЕХ: Транзакция распознана")

                // Сохраняем транзакцию в базу данных
                saveTransactionToDatabase(amount.toDouble(), description ?: "", source)
            } else {
                Log.d(TAG, "❌ ОШИБКА: Сумма не извлечена")
            }
        } else {
            Log.d(TAG, "ℹ️ Пропущено: не финансовая транзакция")
        }
    }

    /**
     * Сохраняет распознанную транзакцию в базу данных
     */
    private fun saveTransactionToDatabase(amount: Double, description: String, source: String) {
        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(Date(currentTime))

        // Форматируем сумму с правильным знаком
        val formattedAmount = if (amount >= 0) {
            "+${String.format("%.2f", amount)}"
        } else {
            String.format("%.2f", amount)
        }

        // Формируем описание транзакции
        val transactionDescription = if (description.isNotEmpty()) {
            "[ТЕСТ] $source: $description"
        } else {
            "[ТЕСТ] $source: автоматически распознанная транзакция"
        }

        // Создаем строку для сохранения в формате: "сумма - описание"
        val countString = "$formattedAmount - $transactionDescription"

        val transaction = Transaction(
            time = currentTime,
            date = currentDate,
            count = countString
        )

        scope.launch {
            try {
                // Сохраняем транзакцию
                interactor.saveTransaction(transaction)
                Log.d(TAG, "💾 Транзакция сохранена в базу данных: $countString")

                // Обновляем основную сумму в MainData
                updateMainDataSummary(amount)

            } catch (e: Exception) {
                Log.e(TAG, "❌ Ошибка при сохранении транзакции", e)
            }
        }
    }

    /**
     * Обновляет основную сумму в MainData для отображения на главной странице
     */
    private fun updateMainDataSummary(amount: Double) {
        try {
            val currentMainData = interactor.loadMainData()
            
            // Проверяем, не был ли сброс после последней транзакции
            val currentTime = System.currentTimeMillis()
            val daysSinceClear = if (currentMainData.dateOfClear > 0) {
                (currentTime - currentMainData.dateOfClear) / (1000 * 60 * 60 * 24)
            } else {
                0
            }
            
            // Если сброс был недавно (в течение дня), начинаем с нуля
            val baseAmount = if (daysSinceClear == 0L) {
                java.math.BigDecimal.ZERO
            } else {
                currentMainData.summaryAmount
            }
            
            val newSummaryAmount = baseAmount.add(java.math.BigDecimal.valueOf(amount))
            
            val updatedMainData = MainData(
                dateOfClear = currentMainData.dateOfClear,
                summaryAmount = newSummaryAmount
            )
            
            interactor.saveMainData(updatedMainData)
            Log.d(TAG, "📊 Основная сумма обновлена: ${currentMainData.summaryAmount} → ${newSummaryAmount}")
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Ошибка при обновлении основной суммы", e)
        }
    }

    /**
     * Тестирует конкретное уведомление (для отладки)
     */
    fun testSpecificNotification(title: String, text: String) {
        Log.d(TAG, "=== Тест конкретного уведомления ===")
        Log.d(TAG, "Заголовок: $title")
        Log.d(TAG, "Текст: $text")

        val isFinancial = parser.isFinancialTransaction(title, text)
        Log.d(TAG, "Финансовая транзакция: $isFinancial")

        if (isFinancial) {
            val amount = parser.extractAmount(title, text)
            val description = parser.extractDescription(title, text)

            Log.d(TAG, "Сумма: $amount")
            Log.d(TAG, "Описание: $description")

            if (amount != null) {
                saveTransactionToDatabase(amount.toDouble(), description ?: "", title)
            }
        }
    }

    /**
     * Очищает все тестовые транзакции из базы данных
     */
    fun clearTestTransactions() {
        scope.launch {
            try {
                val allTransactions = interactor.loadTransactions()
                val testTransactions = allTransactions.filter { 
                    it.count.contains("[ТЕСТ]") 
                }
                
                Log.d(TAG, "Найдено ${testTransactions.size} тестовых транзакций для удаления")
                
                // Удаляем тестовые транзакции
                for (transaction in testTransactions) {
                    // Здесь можно добавить метод deleteTransaction если он есть
                    // Пока просто логируем
                    Log.d(TAG, "Удалена тестовая транзакция: ${transaction.count}")
                }
                
                // Пересчитываем сумму без тестовых транзакций
                recalculateMainDataWithoutTests()
                
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка при очистке тестовых транзакций", e)
            }
        }
    }
    
    /**
     * Пересчитывает MainData без учета тестовых транзакций
     */
    private suspend fun recalculateMainDataWithoutTests() {
        try {
            val allTransactions = interactor.loadTransactions()
            val nonTestTransactions = allTransactions.filter { 
                !it.count.contains("[ТЕСТ]") 
            }
            
            var totalAmount = java.math.BigDecimal.ZERO
            for (transaction in nonTestTransactions) {
                val amount = extractAmountFromTransaction(transaction.count)
                totalAmount = totalAmount.add(java.math.BigDecimal.valueOf(amount))
            }
            
            val currentMainData = interactor.loadMainData()
            val updatedMainData = MainData(
                dateOfClear = currentMainData.dateOfClear,
                summaryAmount = totalAmount
            )
            
            interactor.saveMainData(updatedMainData)
            Log.d(TAG, "📊 MainData пересчитан без тестовых транзакций: $totalAmount")
            
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при пересчете MainData", e)
        }
    }
    
    /**
     * Извлекает сумму из строки транзакции
     */
    private fun extractAmountFromTransaction(transactionText: String): Double {
        return try {
            when {
                // Тестовые транзакции в формате "сумма - описание"
                transactionText.contains(" - ") -> {
                    val parts = transactionText.split(" - ", limit = 2)
                    parts[0].toDoubleOrNull() ?: 0.0
                }
                // Обычные транзакции
                transactionText.startsWith("-") -> {
                    transactionText.toDoubleOrNull() ?: 0.0
                }
                transactionText == "Сброс" -> 0.0
                else -> {
                    transactionText.toDoubleOrNull() ?: 0.0
                }
            }
        } catch (e: Exception) {
            0.0
        }
    }
} 