package com.example.moneyeverydayxml.app

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

    private val _calculatorDataUpdated = MutableLiveData<Boolean>()
    val calculatorDataUpdated: LiveData<Boolean> = _calculatorDataUpdated

    private val _historyDataUpdated = MutableLiveData<Boolean>()
    val historyDataUpdated: LiveData<Boolean> = _historyDataUpdated

    init {
        checkNotificationPermissions()
    }

    fun checkNotificationPermissions() {
        val isEnabled = notificationPermissionManager.isNotificationServiceEnabled()
        _notificationPermissionRequired.postValue(!isEnabled)
    }

    fun requestNotificationPermission() {
        notificationPermissionManager.requestNotificationPermission()
    }

    fun onCalculatorDataUpdated() {
        _calculatorDataUpdated.value = false
    }

    fun onNewTransactionReceived() {
        _historyDataUpdated.postValue(true)
        _calculatorDataUpdated.postValue(true)
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