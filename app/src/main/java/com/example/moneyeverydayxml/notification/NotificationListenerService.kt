package com.example.moneyeverydayxml.notification

import android.app.Notification
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.moneyeverydayxml.core.domain.RepositoryInterface
import com.example.moneyeverydayxml.core.domain.model.Transaction
import com.example.moneyeverydayxml.notification.parser.NotificationParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class NotificationListenerService : NotificationListenerService(), KoinComponent {

    private val parser = NotificationParser()
    private val scope = CoroutineScope(Dispatchers.IO)
    private val repository: RepositoryInterface by inject()

    private var lastTransactionTime: Long = 0
    private var lastTransactionAmount: String = ""

    companion object {
        private const val TAG = "NotificationListener"
        const val ACTION_TRANSACTION_ADDED = "com.example.moneyeverydayxml.TRANSACTION_ADDED"
        private const val DEBOUNCE_PERIOD_MS = 1000 // 1 секунда
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "NotificationListenerService создан")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)

        val packageName = sbn.packageName
        val notification = sbn.notification
        val title = notification.extras.getString(Notification.EXTRA_TITLE) ?: ""
        val text = notification.extras.getString(Notification.EXTRA_TEXT) ?: ""

        Log.d(TAG, "Получено уведомление от: $packageName")
        Log.d(TAG, "Заголовок: $title")
        Log.d(TAG, "Текст: $text")

        if (!parser.isFinancialTransaction(title, text)) {
            Log.d(TAG, "Уведомление не содержит финансовой информации")
            return
        }

        val amount = parser.extractAmount(title, text)

        if (amount != null) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastTransactionTime < DEBOUNCE_PERIOD_MS && amount.toString() == lastTransactionAmount) {
                Log.d(TAG, "Обнаружен дубликат транзакции по сумме, игнорируем.")
                return
            }

            lastTransactionTime = currentTime
            lastTransactionAmount = amount.toString()

            val formattedTime =
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM, EEEE, HH:mm", Locale("ru")))
            val timeInMillis = System.currentTimeMillis()

            val transaction = Transaction(
                id = null,
                time = timeInMillis,
                date = formattedTime,
                count = amount.toString(),
                description = "$title\n$text"
            )
            Log.d(TAG, "Обнаружена финансовая транзакция: ${transaction.count} руб.")
            saveTransaction(transaction)
        }
    }

    private fun saveTransaction(transaction: Transaction) {
        scope.launch {
            try {
                repository.addTransactionAndUpdateSummary(transaction)
                Log.i(
                    TAG,
                    "Сохранена транзакция в БД: ${transaction.count} руб. - ${transaction.date}"
                )
                val intent = Intent(ACTION_TRANSACTION_ADDED).apply {
                    putExtra("amount", transaction.count)
                    putExtra("description", transaction.description)
                    putExtra("date", transaction.date)
                    setPackage(this@NotificationListenerService.packageName)
                }
                sendBroadcast(intent)

            } catch (e: Exception) {
                Log.e(TAG, "Ошибка при сохранении транзакции", e)
            }
        }
    }
} 