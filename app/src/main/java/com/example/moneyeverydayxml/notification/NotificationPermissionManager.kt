package com.example.moneyeverydayxml.notification

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log

class NotificationPermissionManager(private val context: Context) {

    companion object {
        private const val TAG = "NotificationPermissionManager"
    }

    fun isNotificationServiceEnabled(): Boolean {
        val flat = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners"
        )
        return flat?.contains(context.packageName) == true
    }

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

    fun requestNotificationPermission() {
        if (!isNotificationServiceEnabled()) {
            openNotificationSettings()
        }
    }
}