package com.example.moneyeverydayxml

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.moneyeverydayxml.databinding.ActivityMainBinding
import com.example.moneyeverydayxml.history.HistoryActivity
import com.example.moneyeverydayxml.history.Savings
import com.example.moneyeverydayxml.history.readSavingsBySharedPref
import com.example.moneyeverydayxml.history.saveSavingsBySharedPref
import java.text.SimpleDateFormat
import java.util.Calendar

const val MONTH_SUMMARY_PREF_KEY = "month_summ_key"
const val DAY_OF_CLEAR_PREF_KEY = "day_of_clear__key"
const val SAVINGS_CLASS_SAVE_KEY = "savings_save_key"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var summarySave: SharedPreferences
    private lateinit var savingsClassSave: SharedPreferences

    private var sumCalculate: Int = 0
    private var dayFromClear: Int = 0
    private var inputToInt: Int = 0
    private var lastInput: Int = 0
    private var savings = Savings()
    private val current = Calendar.getInstance().timeInMillis
    private val formatter = SimpleDateFormat("dd MMMM yyyy: HH mm")
    private var dateOfClear: Long = 0
    private var monthByDay: Int = 0

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

        val dateOfClearSave = getSharedPreferences(DAY_OF_CLEAR_PREF_KEY, MODE_PRIVATE)
        dateOfClear = dateOfClearSave.getLong(DAY_OF_CLEAR_PREF_KEY, current)

        summarySave = getSharedPreferences(MONTH_SUMMARY_PREF_KEY, MODE_PRIVATE)
        sumCalculate = summarySave.getInt(MONTH_SUMMARY_PREF_KEY, 0)
        binding.monthSummary.text = sumCalculate.toString()

        savingsClassSave = getSharedPreferences(SAVINGS_CLASS_SAVE_KEY, MODE_PRIVATE)
        savings = readSavingsBySharedPref(savingsClassSave)

        binding.today.text = formatter.format(current)
        dayFromClear = (((current - dateOfClear) / (1000 * 60 * 60 * 24)) + 1).toInt()
        binding.daysPassed.text = (dayFromClear).toString()

        monthByDay = sumCalculate / dayFromClear
        binding.perDay.text = monthByDay.toString()

        setOnClickListeners(dateOfClearSave)
    }

    private fun setOnClickListeners(dateOfClearSave: SharedPreferences) {
        binding.increaseButton.setOnClickListener {
            increaseAction()
        }
        binding.decreaseButton.setOnClickListener {
            decreaseAction()
        }
        binding.clearButton.setOnClickListener {
            sumCalculate = 0
            binding.monthSummary.text = "0"
            dateOfClear = current
            monthByDay = sumCalculate / dayFromClear
            binding.perDay.text = monthByDay.toString()
            summarySave.edit().putInt(MONTH_SUMMARY_PREF_KEY, sumCalculate).apply()
            dateOfClearSave.edit().putLong(DAY_OF_CLEAR_PREF_KEY, dateOfClear).apply()
        }
        binding.historyButton.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
    }

    private fun decreaseAction() {
        inputToInt = binding.inputCount.text.toString().toInt()
        sumCalculate -= inputToInt
        binding.monthSummary.text = sumCalculate.toString()
        lastInput = -inputToInt
        savings.saveOperation()
        monthByDay = sumCalculate / dayFromClear
        binding.perDay.text = monthByDay.toString()
        summarySave.edit().putInt(MONTH_SUMMARY_PREF_KEY, sumCalculate).apply()
        saveSavingsBySharedPref(savings, savingsClassSave)
        binding.inputCount.setText("")
    }

    private fun increaseAction() {
        inputToInt = binding.inputCount.text.toString().toInt()
        sumCalculate += inputToInt
        binding.monthSummary.text = sumCalculate.toString()
        lastInput = inputToInt
        savings.saveOperation()
        monthByDay = sumCalculate / dayFromClear
        binding.perDay.text = monthByDay.toString()
        summarySave.edit().putInt(MONTH_SUMMARY_PREF_KEY, sumCalculate).apply()
        saveSavingsBySharedPref(savings, savingsClassSave)
        binding.inputCount.setText("")
    }
}
