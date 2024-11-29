package com.example.moneyeverydayxml.history.ui

import android.view.View
import android.widget.TextView
import com.example.moneyeverydayxml.R
import androidx.recyclerview.widget.RecyclerView
import com.example.moneyeverydayxml.history.domain.model.Transaction
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val formatter = SimpleDateFormat("dd MMM, EEEE, HH:mm", Locale("ru"))

    private val date: TextView = itemView.findViewById<TextView>(R.id.transaction_date)
    private val count: TextView = itemView.findViewById<TextView>(R.id.transaction_count)

    fun bind(model: Transaction){
        date.text = formatter.format(model.date)
        count.text = model.count.setScale(2, RoundingMode.DOWN).toString()
    }
}