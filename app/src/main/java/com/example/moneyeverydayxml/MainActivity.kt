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

const val MONTH_SUMMARY_PREF_KEY = "month_summ_key"
const val INDEX_SAVE_KEY = "index_save_key"
var input: Int = 0
val savings = Savings()

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

        val sharedPrefIndex = getSharedPreferences(INDEX_SAVE_KEY, MODE_PRIVATE)

        increaseButton.setOnClickListener {
            sumCalculate += inputField.text.toString().toInt()
            monthSummary.text = sumCalculate.toString()
            input = inputField.text.toString().toInt()
            inputField.setText("")
            savings.saveOperation(input)
            sharedPrefSum.edit()
                .putInt(MONTH_SUMMARY_PREF_KEY, sumCalculate)
                .apply()
        }
        decreaseButton.setOnClickListener {
            sumCalculate -= inputField.text.toString().toInt()
            monthSummary.text = sumCalculate.toString()
            inputField.setText("0")
            sharedPrefSum.edit()
                .putInt(MONTH_SUMMARY_PREF_KEY, sumCalculate)
                .apply()
        }
        clearButton.setOnClickListener {
            sumCalculate = 0
            monthSummary.text = "0"
            sharedPrefSum.edit()
                .putInt(MONTH_SUMMARY_PREF_KEY, sumCalculate)
                .apply()
        }
        historyButton.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
    }
}
