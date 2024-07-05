package com.example.moneyeverydayxml

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.moneyeverydayxml.history.HistoryActivity
import com.example.moneyeverydayxml.history.Savings
import com.example.moneyeverydayxml.history.readSavingsBySharedPref
import com.example.moneyeverydayxml.history.saveSavingsBySharedPref
import java.text.DateFormat.getDateInstance
import java.text.DateFormat.getDateTimeInstance
import java.text.SimpleDateFormat
import java.util.Calendar

const val MONTH_SUMMARY_PREF_KEY = "month_summ_key"
const val DAY_OF_CLEAR_PREF_KEY = "day_of_clear__key"
const val SAVINGS_CLASS_SAVE_KEY = "savings_save_key"

var lastInput: Int = 0
var savings = Savings()
val current = Calendar.getInstance().timeInMillis
val formatter = SimpleDateFormat("dd MMMM yyyy: HH mm")
var dateOfClear: Long = 0
var monthByDay: Int = 0

//val d = getDateInstance()
//val v = getDateTimeInstance()

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
        val today = findViewById<TextView>(R.id.today)
        val perDay = findViewById<TextView>(R.id.perDay)
        val daysPassed = findViewById<TextView>(R.id.daysPassed)
        var sumCalculate: Int


        val dateOfClearSave = getSharedPreferences(DAY_OF_CLEAR_PREF_KEY, MODE_PRIVATE)
        dateOfClear = dateOfClearSave.getLong(DAY_OF_CLEAR_PREF_KEY, current)

        val summarySave = getSharedPreferences(MONTH_SUMMARY_PREF_KEY, MODE_PRIVATE)
        sumCalculate = summarySave.getInt(MONTH_SUMMARY_PREF_KEY, 0)
        monthSummary.text = sumCalculate.toString()

        val savingsClassSave = getSharedPreferences(SAVINGS_CLASS_SAVE_KEY, MODE_PRIVATE)
        if (savingsClassSave != null) {
            savings = readSavingsBySharedPref(savingsClassSave)
        }

        today.text = formatter.format(current)
        val dayFromClear: Int = (((current - dateOfClear) / (1000 * 60 * 60 * 24)) + 1).toInt()
        daysPassed.text = (dayFromClear).toString()

        monthByDay = sumCalculate / dayFromClear
        perDay.text = monthByDay.toString()

        increaseButton.setOnClickListener {
            val inputToInt = inputField.text.toString().toInt()
            sumCalculate += inputToInt
            monthSummary.text = sumCalculate.toString()
            lastInput = inputToInt
            savings.saveOperation()
            monthByDay = sumCalculate / dayFromClear
            perDay.text = monthByDay.toString()
            summarySave.edit().putInt(MONTH_SUMMARY_PREF_KEY, sumCalculate).apply()
            saveSavingsBySharedPref(savings, savingsClassSave)
            inputField.setText("")
        }
        decreaseButton.setOnClickListener {
            val inputToInt = inputField.text.toString().toInt()
            sumCalculate -= inputToInt
            monthSummary.text = sumCalculate.toString()
            lastInput = -inputToInt
            savings.saveOperation()
            monthByDay = sumCalculate / dayFromClear
            perDay.text = monthByDay.toString()
            summarySave.edit().putInt(MONTH_SUMMARY_PREF_KEY, sumCalculate).apply()
            saveSavingsBySharedPref(savings, savingsClassSave)
            inputField.setText("")
        }
        clearButton.setOnClickListener {
            sumCalculate = 0
            monthSummary.text = "0"
            dateOfClear = current
            monthByDay = sumCalculate / dayFromClear
            perDay.text = monthByDay.toString()
            summarySave.edit()
                .putInt(MONTH_SUMMARY_PREF_KEY, sumCalculate)
                .apply()
            dateOfClearSave.edit().putLong(DAY_OF_CLEAR_PREF_KEY, dateOfClear).apply()
        }
        historyButton.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
    }
}
