package com.example.moneyeverydayxml.notification

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log

class NotificationPermissionManager(private val context: Context) {
    
    companion object {
        private const val TAG = "NotificationPermissionManager"
    }
    
    /**
     * Проверяет, включен ли сервис прослушивания уведомлений
     */
    fun isNotificationServiceEnabled(): Boolean {
        val flat = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners"
        )
        
        return flat?.contains(context.packageName) == true
    }
    
    /**
     * Открывает настройки для включения сервиса уведомлений
     */
    fun openNotificationSettings() {
        try {
            val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при открытии настроек уведомлений", e)
            // Fallback - открываем общие настройки
            val intent = Intent(Settings.ACTION_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
    
    /**
     * Запрашивает разрешение на прослушивание уведомлений
     */
    fun requestNotificationPermission() {
        if (!isNotificationServiceEnabled()) {
            openNotificationSettings()
        }
    }
    
    /**
     * Получает статус сервиса
     */
    fun getServiceStatus(): ServiceStatus {
        return if (isNotificationServiceEnabled()) {
            ServiceStatus.ENABLED
        } else {
            ServiceStatus.DISABLED
        }
    }
    
    enum class ServiceStatus {
        ENABLED,
        DISABLED
    }
} 