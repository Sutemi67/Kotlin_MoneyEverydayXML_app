package com.example.moneyeverydayxml.app

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.moneyeverydayxml.R
import com.example.moneyeverydayxml.databinding.ActivityMainBinding
import com.example.moneyeverydayxml.notification.NotificationListenerService
import com.example.moneyeverydayxml.notification.NotificationPermissionManager
import com.example.moneyeverydayxml.notification.parser.NotificationParserTest
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var tabLayoutMediator: TabLayoutMediator
    private val notificationPermissionManager: NotificationPermissionManager by inject()

    private val transactionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == NotificationListenerService.ACTION_TRANSACTION_ADDED) {
                val amount = intent.getStringExtra("amount") ?: ""
                val description = intent.getStringExtra("description") ?: ""
                
                Snackbar.make(
                    binding.root,
                    getString(R.string.transaction_added_auto, amount, description),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.pagerLayout.adapter = AdapterFragment(supportFragmentManager, lifecycle)
        tabLayoutMediator =
            TabLayoutMediator(binding.tabLayout, binding.pagerLayout) { tab, position ->
                when (position) {
                    0 -> tab.text = "Калькулятор"
                    else -> tab.text = "История операций"
                }
            }
        tabLayoutMediator.attach()

        // Настройка кнопки тестирования
        setupTestButton()

        // Проверяем разрешения на уведомления
        checkNotificationPermissions()

        // Регистрируем receiver для получения уведомлений о транзакциях
        val filter = IntentFilter(NotificationListenerService.ACTION_TRANSACTION_ADDED)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(transactionReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(transactionReceiver, filter)
        }

        // Запускаем тесты парсера в debug режиме
        if (isDebugMode()) {
            runParserTests()
        }
    }

    private fun setupTestButton() {
        // Показываем кнопку только в debug режиме
        if (isDebugMode()) {
            binding.testNotificationButton.visibility = View.VISIBLE
            
            binding.testNotificationButton.setOnClickListener {
                runNotificationTests()
            }
        }
    }

    private fun runNotificationTests() {
        try {
            val testParser = NotificationParserTest()
            testParser.testNotificationFormats()
            
            Snackbar.make(
                binding.root,
                getString(R.string.test_notification_success),
                Snackbar.LENGTH_LONG
            ).setAction(getString(R.string.view_test_results)) {
                // Показываем информацию о сохраненных транзакциях
                showTestTransactionsInfo()
            }.show()
            
            // Обновляем данные калькулятора после тестов
            updateCalculatorData()
            
            Log.d("MainActivity", "Тесты уведомлений запущены пользователем")
        } catch (e: Exception) {
            Log.e("MainActivity", "Ошибка при выполнении тестов уведомлений", e)
            
            Snackbar.make(
                binding.root,
                getString(R.string.test_notification_error, e.message),
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun showTestTransactionsInfo() {
        // Показываем информацию о тестовых транзакциях
        Snackbar.make(
            binding.root,
            getString(R.string.test_transactions_saved),
            Snackbar.LENGTH_LONG
        ).setAction(getString(R.string.clear_test_transactions)) {
            clearTestTransactions()
        }.show()
    }
    
    private fun clearTestTransactions() {
        try {
            val testParser = NotificationParserTest()
            testParser.clearTestTransactions()
            
            Snackbar.make(
                binding.root,
                getString(R.string.test_transactions_cleared),
                Snackbar.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            Log.e("MainActivity", "Ошибка при очистке тестовых транзакций", e)
        }
    }

    private fun updateCalculatorData() {
        // Переключаемся на вкладку калькулятора для обновления данных
        binding.pagerLayout.setCurrentItem(0, true)
        
        // Небольшая задержка для обновления UI
        binding.root.postDelayed({
            // Обновляем данные калькулятора
            try {
                // Получаем ViewModel калькулятора и обновляем данные
                val calculatorFragment = supportFragmentManager.fragments.firstOrNull { 
                    it is com.example.moneyeverydayxml.calculator.ui.CalculatorFragment 
                } as? com.example.moneyeverydayxml.calculator.ui.CalculatorFragment
                
                calculatorFragment?.let { fragment ->
                    // Обновляем данные через ViewModel
                    fragment.refreshCalculatorData()
                }
                
                Log.d("MainActivity", "Данные калькулятора обновлены после тестов")
            } catch (e: Exception) {
                Log.e("MainActivity", "Ошибка при обновлении данных калькулятора", e)
            }
        }, 500)
        
        // Показываем уведомление об обновлении данных
        Snackbar.make(
            binding.root,
            getString(R.string.calculator_data_updated),
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun checkNotificationPermissions() {
        if (!notificationPermissionManager.isNotificationServiceEnabled()) {
            Snackbar.make(
                binding.root,
                getString(R.string.notification_permission_required),
                Snackbar.LENGTH_LONG
            ).setAction(getString(R.string.configure)) {
                notificationPermissionManager.requestNotificationPermission()
            }.show()
        }
    }

    private fun isDebugMode(): Boolean {
        // Используем альтернативный способ проверки debug режима
        return (applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
    }

    private fun runParserTests() {
        try {
            val testParser = NotificationParserTest()
            testParser.testNotificationFormats()
            Log.d("MainActivity", "Тесты парсера выполнены")
        } catch (e: Exception) {
            Log.e("MainActivity", "Ошибка при выполнении тестов парсера", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tabLayoutMediator.detach()
        unregisterReceiver(transactionReceiver)
    }

    override fun onResume() {
        super.onResume()
        // Проверяем статус сервиса при возвращении в приложение
        checkNotificationPermissions()
    }
}
