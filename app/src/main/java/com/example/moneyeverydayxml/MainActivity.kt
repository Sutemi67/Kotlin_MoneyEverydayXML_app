package com.example.moneyeverydayxml

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.moneyeverydayxml.history.HistoryActivity
import com.example.moneyeverydayxml.history.Savings
import com.example.moneyeverydayxml.history.readSavingsBySharedPref
import com.example.moneyeverydayxml.history.saveSavingsBySharedPref
import com.google.type.Date
import java.time.LocalTime
import java.time.format.DateTimeFormatter

const val MONTH_SUMMARY_PREF_KEY = "month_summ_key"
const val SAVINGS_CLASS_SAVE_KEY = "savings_save_key"
var lastInput: Int = 0
var savings = Savings()

//val current = Calendar.getInstance().timeInMillis
//val formatter = SimpleDateFormat("dd MMMM yyyy")
//val daysPass = ((current - setDateOfClear.value) / (1000 * 60 * 60 * 24)) + 1

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val increaseButton = findViewById<Button>(R.id.increaseButton)
        val decreaseButton = findViewById<Button>(R.id.decreaseButton)
        val clearButton = findViewById<Button>(R.id.clearButton)
        val historyButton = findViewById<Button>(R.id.historyButton)
        val inputField = findViewById<EditText>(R.id.inputCount)
        val monthSummary = findViewById<TextView>(R.id.monthSummary)
        var sumCalculate: Int
        var monthByDay: Int
        var dayFromClear: Int


        val sharedPrefSum = getSharedPreferences(MONTH_SUMMARY_PREF_KEY, MODE_PRIVATE)
        sumCalculate = sharedPrefSum.getInt(MONTH_SUMMARY_PREF_KEY, 0)
        monthSummary.text = sumCalculate.toString()

        val sharedPrefSavings = getSharedPreferences(SAVINGS_CLASS_SAVE_KEY, MODE_PRIVATE)
        if (readSavingsBySharedPref(sharedPrefSavings) != null) {
            savings = readSavingsBySharedPref(sharedPrefSavings)
        }
        increaseButton.setOnClickListener {
            val inputToInt = inputField.text.toString().toInt()
            sumCalculate += inputToInt
            monthSummary.text = sumCalculate.toString()
            lastInput = inputToInt
            savings.saveOperation()
            sharedPrefSum.edit().putInt(MONTH_SUMMARY_PREF_KEY, sumCalculate).apply()
            saveSavingsBySharedPref(savings, sharedPrefSavings)
            inputField.setText("")
        }
        decreaseButton.setOnClickListener {
            val inputToInt = inputField.text.toString().toInt()
            sumCalculate -= inputToInt
            monthSummary.text = sumCalculate.toString()
            lastInput = -inputToInt
            savings.saveOperation()
            sharedPrefSum.edit().putInt(MONTH_SUMMARY_PREF_KEY, sumCalculate).apply()
            inputField.setText("")
        }
        clearButton.setOnClickListener {
            sumCalculate = 0
            monthSummary.text = "0"
            sharedPrefSum.edit().putInt(MONTH_SUMMARY_PREF_KEY, sumCalculate).apply()
        }
        historyButton.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
    }
}
