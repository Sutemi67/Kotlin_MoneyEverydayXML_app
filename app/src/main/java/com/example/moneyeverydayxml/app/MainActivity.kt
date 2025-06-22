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
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var tabLayoutMediator: TabLayoutMediator
    private val viewModel: MainViewModel by viewModel()

    private val transactionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == NotificationListenerService.ACTION_TRANSACTION_ADDED) {
                val amount = intent.getStringExtra("amount") ?: ""
                val description = intent.getStringExtra("description") ?: ""

                viewModel.onNewTransactionReceived()

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

        setupUI()
        setupObservers()
        setupTestButton()

        // Регистрируем receiver для получения уведомлений о транзакциях
        registerTransactionReceiver()

        // Запускаем тесты парсера в debug режиме
        if (viewModel.isDebugMode(this)) {
            runParserTests()
        }
    }

    private fun setupUI() {
        binding.pagerLayout.adapter = AdapterFragment(supportFragmentManager, lifecycle)
        tabLayoutMediator =
            TabLayoutMediator(binding.tabLayout, binding.pagerLayout) { tab, position ->
                when (position) {
                    0 -> tab.text = "Калькулятор"
                    else -> tab.text = "История операций"
                }
            }
        tabLayoutMediator.attach()
    }

    private fun setupObservers() {
        // Наблюдаем за результатами тестирования уведомлений
        viewModel.testNotificationResult.observe(this) { result ->
            when (result) {
                is MainViewModel.TestNotificationResult.Success -> {
                    showSnackbar(result.message, Snackbar.LENGTH_LONG) {
                        setAction(getString(R.string.view_test_results)) {
                            viewModel.showTestTransactionsInfo()
                        }
                    }
                }

                is MainViewModel.TestNotificationResult.Error -> {
                    showSnackbar(result.message, Snackbar.LENGTH_LONG)
                }

                is MainViewModel.TestNotificationResult.Info -> {
                    showSnackbar(result.message, Snackbar.LENGTH_LONG) {
                        setAction(getString(R.string.clear_test_transactions)) {
                            viewModel.clearTestTransactions()
                        }
                    }
                }
            }
        }

        // Наблюдаем за обновлением данных калькулятора
        viewModel.calculatorDataUpdated.observe(this) { updated ->
            if (updated) {
                viewModel.onCalculatorDataUpdated() // Сбрасываем флаг
            }
        }

        // Наблюдаем за требованиями разрешений
        viewModel.notificationPermissionRequired.observe(this) { required ->
            if (required) {
                showPermissionSnackbar()
            }
        }
    }

    private fun setupTestButton() {
        // Показываем кнопку только в debug режиме
        if (viewModel.isDebugMode(this)) {
            binding.testNotificationButton.visibility = View.VISIBLE

            binding.testNotificationButton.setOnClickListener {
                viewModel.runNotificationTests()
            }
        }
    }

    private fun showSnackbar(
        message: String,
        duration: Int,
        action: (Snackbar.() -> Unit)? = null
    ) {
        val snackbar = Snackbar.make(binding.root, message, duration)
        action?.invoke(snackbar)
        snackbar.show()
    }

    private fun showPermissionSnackbar() {
        showSnackbar(
            getString(R.string.notification_permission_required),
            Snackbar.LENGTH_LONG
        ) {
            setAction(getString(R.string.configure)) {
                viewModel.requestNotificationPermission()
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun registerTransactionReceiver() {
        val filter = IntentFilter(NotificationListenerService.ACTION_TRANSACTION_ADDED)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(transactionReceiver, filter, RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(transactionReceiver, filter)
        }
    }

    private fun runParserTests() {
        try {
            val testParser =
                com.example.moneyeverydayxml.notification.parser.NotificationParserTest()
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
        viewModel.checkNotificationPermissions()
    }
}
