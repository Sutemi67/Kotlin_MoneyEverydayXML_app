package com.example.moneyeverydayxml.app

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyeverydayxml.notification.NotificationPermissionManager
import com.example.moneyeverydayxml.notification.parser.NotificationParserTest
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class MainViewModel : ViewModel() {
    
    private val notificationPermissionManager: NotificationPermissionManager by inject(NotificationPermissionManager::class.java)
    
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
     * Запускает тестирование уведомлений
     */
    fun runNotificationTests() {
        viewModelScope.launch {
            try {
                val testParser = NotificationParserTest()
                testParser.testNotificationFormats()
                
                _testNotificationResult.postValue(
                    TestNotificationResult.Success("Тесты уведомлений выполнены! Проверьте логи")
                )
                
                Log.d("MainViewModel", "Тесты уведомлений запущены пользователем")
                
            } catch (e: Exception) {
                Log.e("MainViewModel", "Ошибка при выполнении тестов уведомлений", e)
                
                _testNotificationResult.postValue(
                    TestNotificationResult.Error("Ошибка при выполнении тестов: ${e.message}")
                )
            }
        }
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
     * Очищает тестовые транзакции
     */
    fun clearTestTransactions() {
        viewModelScope.launch {
            try {
                val testParser = NotificationParserTest()
                testParser.clearTestTransactions()
                
                _testNotificationResult.postValue(
                    TestNotificationResult.Info("Тестовые транзакции помечены для удаления. Перезапустите приложение для применения изменений.")
                )
                
            } catch (e: Exception) {
                Log.e("MainViewModel", "Ошибка при очистке тестовых транзакций", e)
                
                _testNotificationResult.postValue(
                    TestNotificationResult.Error("Ошибка при очистке тестовых транзакций: ${e.message}")
                )
            }
        }
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