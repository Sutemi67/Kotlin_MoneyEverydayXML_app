package com.example.moneyeverydayxml.app

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moneyeverydayxml.notification.NotificationPermissionManager
import org.koin.java.KoinJavaComponent.inject

class MainViewModel : ViewModel() {

    private val notificationPermissionManager: NotificationPermissionManager by inject(
        NotificationPermissionManager::class.java
    )

    private val _notificationPermissionRequired = MutableLiveData<Boolean>()
    val notificationPermissionRequired: LiveData<Boolean> = _notificationPermissionRequired

    private val _testNotificationResult = MutableLiveData<TestNotificationResult>()
    val testNotificationResult: LiveData<TestNotificationResult> = _testNotificationResult

    private val _calculatorDataUpdated = MutableLiveData<Boolean>()
    val calculatorDataUpdated: LiveData<Boolean> = _calculatorDataUpdated

    private val _historyDataUpdated = MutableLiveData<Boolean>()
    val historyDataUpdated: LiveData<Boolean> = _historyDataUpdated

    init {
        checkNotificationPermissions()
    }

    /**
     * Проверяет разрешения на уведомления
     */
    fun checkNotificationPermissions() {
        val isEnabled = notificationPermissionManager.isNotificationServiceEnabled()
        _notificationPermissionRequired.postValue(!isEnabled)
    }

    /**
     * Запрашивает разрешение на доступ к уведомлениям
     */
    fun requestNotificationPermission() {
        notificationPermissionManager.requestNotificationPermission()
    }

    /**
     * Показывает информацию о тестовых транзакциях
     */
    fun showTestTransactionsInfo() {
        _testNotificationResult.postValue(
            TestNotificationResult.Info("Тестовые транзакции сохранены в истории. Перейдите на вкладку 'История операций' для просмотра.")
        )
    }

    /**
     * Сбрасывает флаг обновления данных калькулятора
     */
    fun onCalculatorDataUpdated() {
        _calculatorDataUpdated.value = false
    }

    /**
     * Сигнализирует о поступлении новой транзакции
     */
    fun onNewTransactionReceived() {
        _historyDataUpdated.postValue(true)
        _calculatorDataUpdated.postValue(true)
    }

    /**
     * Проверяет, находится ли приложение в debug режиме
     */
    fun isDebugMode(context: Context): Boolean {
        return try {
            // Используем альтернативный способ проверки debug режима
            val applicationInfo = context.applicationInfo
            (applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Результат тестирования уведомлений
     */
    sealed class TestNotificationResult {
        data class Success(val message: String) : TestNotificationResult()
        data class Error(val message: String) : TestNotificationResult()
        data class Info(val message: String) : TestNotificationResult()
    }
} 