package com.example.moneyeverydayxml.history

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.moneyeverydayxml.R
import com.example.moneyeverydayxml.savings

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_history)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.history)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backButton = findViewById<Button>(R.id.backbutton)
        backButton.setOnClickListener { finish() }

        findViewById<TextView>(R.id.date1).text = savings.operationsDates[0]
        findViewById<TextView>(R.id.date2).text = savings.operationsDates[1]
        findViewById<TextView>(R.id.date3).text = savings.operationsDates[2]
        findViewById<TextView>(R.id.date4).text = savings.operationsDates[3]
        findViewById<TextView>(R.id.date5).text = savings.operationsDates[4]

        findViewById<TextView>(R.id.count1).text = savings.operationsCounts[0].toString()
        findViewById<TextView>(R.id.count2).text = savings.operationsCounts[1].toString()
        findViewById<TextView>(R.id.count3).text = savings.operationsCounts[2].toString()
        findViewById<TextView>(R.id.count4).text = savings.operationsCounts[3].toString()
        findViewById<TextView>(R.id.count5).text = savings.operationsCounts[4].toString()

    }
}