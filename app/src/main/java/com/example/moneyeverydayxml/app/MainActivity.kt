package com.example.moneyeverydayxml.app

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
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
    private lateinit var themeManager: ThemeManager

    private val transactionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == NotificationListenerService.ACTION_TRANSACTION_ADDED) {
                viewModel.onNewTransactionReceived()
                val message = getString(R.string.transaction_added_auto)
                showSnackbar(message)
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

        // Инициализация ThemeManager и применение сохраненной темы
        themeManager = ThemeManager(this)
        AppCompatDelegate.setDefaultNightMode(themeManager.getCurrentThemeMode())
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
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_settings -> {
                    themeManager.toggleTheme()
                    val themeMessage = if (themeManager.isDarkTheme()) {
                        "Темная тема включена"
                    } else {
                        "Светлая тема включена"
                    }
                    Toast.makeText(this, themeMessage, Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.action_share -> {
                    // TODO: Share something
                    Toast.makeText(this, "Share clicked", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.action_about -> {
                    AppComponents.aboutAppDialog(this)
                    true
                }

                R.id.action_connect -> {
                    openTelegramLink()
                    true
                }

                R.id.action_donate -> {
                    openDonateLink()
                    true
                }

                else -> false
            }
        }
    }

    private fun setupObservers() {
        viewModel.calculatorDataUpdated.observe(this) { updated ->
            if (updated) {
                viewModel.onCalculatorDataUpdated()
            }
        }
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
        val textView =
            snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.maxLines = 5
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

    private fun openDonateLink() {
        try {
            val donateUrl = "https://pay.cloudtips.ru/p/2d71d3e5"
            val intent = Intent(Intent.ACTION_VIEW, donateUrl.toUri())
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(
                    this,
                    "Не найдено приложение для открытия ссылок",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(this, "Не найдено приложение для открытия ссылок", Toast.LENGTH_SHORT)
                .show()
        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка при открытии ссылки: ${e.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun openTelegramLink() {
        try {
            val telegramUrl = "https://t.me/appcradle"
            val intent = Intent(Intent.ACTION_VIEW, telegramUrl.toUri())
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(
                    this,
                    "Не найдено приложение для открытия ссылки",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка при открытии ссылки: ${e.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    companion object {
        const val SNACKBAR_DURATION = 5000
    }
}
