package com.example.moneyeverydayxml.notification

import android.app.Notification
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.moneyeverydayxml.calculator.domain.RepositoryInterface
import com.example.moneyeverydayxml.history.domain.model.Transaction
import com.example.moneyeverydayxml.notification.parser.NotificationParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NotificationListenerService : NotificationListenerService(), KoinComponent {

    private val parser = NotificationParser()
    private val scope = CoroutineScope(Dispatchers.IO)
    private val repository: RepositoryInterface by inject()

    companion object {
        private const val TAG = "NotificationListener"
        const val ACTION_TRANSACTION_ADDED = "com.example.moneyeverydayxml.TRANSACTION_ADDED"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "NotificationListenerService создан")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)

        val packageName = sbn.packageName
        val notification = sbn.notification

        // Анализируем все уведомления на предмет финансовых транзакций
        Log.d(TAG, "Получено уведомление от: $packageName")

        val transaction = parseNotification(notification, packageName)
        if (transaction != null) {
            Log.d(TAG, "Обнаружена финансовая транзакция: ${transaction.count} руб.")
            saveTransaction(transaction)
        }
    }

    private fun parseNotification(notification: Notification, packageName: String): Transaction? {
        val title = notification.extras.getString(Notification.EXTRA_TITLE) ?: ""
        val text = notification.extras.getString(Notification.EXTRA_TEXT) ?: ""

        Log.d(TAG, "Заголовок: $title")
        Log.d(TAG, "Текст: $text")

        // Проверяем, содержит ли уведомление информацию о финансовой транзакции
        if (!parser.isFinancialTransaction(title, text)) {
            Log.d(TAG, "Уведомление не содержит финансовой информации")
            return null
        }

        // Пытаемся извлечь сумму из уведомления
        val amount = parser.extractAmount(title, text)
        if (amount != null) {
            val currentTime = LocalDateTime.now()
            val formattedTime = currentTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
            val timeInMillis = System.currentTimeMillis()

            return Transaction(
                time = timeInMillis,
                date = formattedTime,
                count = amount.toString()
            )
        }

        return null
    }

    private fun saveTransaction(transaction: Transaction) {
        scope.launch {
            try {
                repository.addTransactionAndUpdateSummary(transaction)
                Log.i(
                    TAG,
                    "Сохранена транзакция в БД: ${transaction.count} руб. - ${transaction.date}"
                )

                // Отправляем broadcast для обновления UI
                val intent = Intent(ACTION_TRANSACTION_ADDED).apply {
                    putExtra("amount", transaction.count)
                    putExtra("description", transaction.date)
                    putExtra("date", transaction.date)
                    // Устанавливаем пакет для безопасности
                    setPackage(this@NotificationListenerService.packageName)
                }
                sendBroadcast(intent)

            } catch (e: Exception) {
                Log.e(TAG, "Ошибка при сохранении транзакции", e)
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        super.onNotificationRemoved(sbn)
        // Можно добавить логику для обработки удаленных уведомлений
    }
} 