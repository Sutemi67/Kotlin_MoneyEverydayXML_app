package com.example.moneyeverydayxml.app

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
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
        registerTransactionReceiver()
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

    private fun showSnackbar(
        message: String,
        action: (Snackbar.() -> Unit)? = null
    ) {
        val snackbar = Snackbar.make(binding.root, message, SNACKBAR_DURATION)
        action?.invoke(snackbar)
        snackbar.show()
    }

    private fun showPermissionSnackbar() {
        showSnackbar(
            message = getString(R.string.notification_permission_required),
            action = {
                setAction(getString(R.string.configure)) {
                    viewModel.requestNotificationPermission()
                }
            }
        )
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

    override fun onDestroy() {
        super.onDestroy()
        tabLayoutMediator.detach()
        unregisterReceiver(transactionReceiver)
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkNotificationPermissions()
    }

    companion object {
        const val SNACKBAR_DURATION = 5
    }
}
