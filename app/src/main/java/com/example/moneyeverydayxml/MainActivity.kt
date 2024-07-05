package com.example.moneyeverydayxml

import android.content.Intent
import android.content.SharedPreferences
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
    private lateinit var inputField: EditText
    private lateinit var monthSummary: TextView
    private lateinit var perDay: TextView
    private lateinit var today: TextView
    private lateinit var daysPassed: TextView
    private lateinit var increaseButton: Button
    private lateinit var decreaseButton: Button
    private lateinit var clearButton: Button
    private lateinit var historyButton: Button
    private lateinit var summarySave: SharedPreferences
    private lateinit var savingsClassSave: SharedPreferences
    private var sumCalculate: Int = 0
    private var dayFromClear: Int = 0
    private var inputToInt: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        increaseButton = findViewById(R.id.increaseButton)
        decreaseButton = findViewById(R.id.decreaseButton)
        clearButton = findViewById(R.id.clearButton)
        historyButton = findViewById(R.id.historyButton)
        inputField = findViewById(R.id.inputCount)
        monthSummary = findViewById(R.id.monthSummary)
        today = findViewById(R.id.today)
        perDay = findViewById(R.id.perDay)
        daysPassed = findViewById(R.id.daysPassed)
//        var sumCalculate: Int


        val dateOfClearSave = getSharedPreferences(DAY_OF_CLEAR_PREF_KEY, MODE_PRIVATE)
        dateOfClear = dateOfClearSave.getLong(DAY_OF_CLEAR_PREF_KEY, current)

        summarySave = getSharedPreferences(MONTH_SUMMARY_PREF_KEY, MODE_PRIVATE)
        sumCalculate = summarySave.getInt(MONTH_SUMMARY_PREF_KEY, 0)
        monthSummary.text = sumCalculate.toString()

        savingsClassSave = getSharedPreferences(SAVINGS_CLASS_SAVE_KEY, MODE_PRIVATE)
        savings = readSavingsBySharedPref(savingsClassSave)

        today.text = formatter.format(current)
        dayFromClear = (((current - dateOfClear) / (1000 * 60 * 60 * 24)) + 1).toInt()
        daysPassed.text = (dayFromClear).toString()

        monthByDay = sumCalculate / dayFromClear
        perDay.text = monthByDay.toString()

        increaseButton.setOnClickListener {
            increaseAction()
        }
        decreaseButton.setOnClickListener {
            decreaseAction()
        }
        clearButton.setOnClickListener {
            sumCalculate = 0
            monthSummary.text = "0"
            dateOfClear = current
            monthByDay = sumCalculate / dayFromClear
            perDay.text = monthByDay.toString()
            summarySave.edit().putInt(MONTH_SUMMARY_PREF_KEY, sumCalculate).apply()
            dateOfClearSave.edit().putLong(DAY_OF_CLEAR_PREF_KEY, dateOfClear).apply()
        }
        historyButton.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
    }

    private fun decreaseAction() {
        inputToInt = inputField.text.toString().toInt()
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

    private fun increaseAction() {
        inputToInt = inputField.text.toString().toInt()
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
}
