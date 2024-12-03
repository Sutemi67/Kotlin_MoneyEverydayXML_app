package com.example.moneyeverydayxml.history.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moneyeverydayxml.R
import com.example.moneyeverydayxml.history.domain.model.Transaction

class TransactionListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val date: TextView = itemView.findViewById<TextView>(R.id.transaction_date)
    private val count: TextView = itemView.findViewById<TextView>(R.id.transaction_count)
    val imageView: ImageView = itemView.findViewById<ImageView>(R.id.imageView)

    fun bind(model: Transaction) {
        date.text = model.date
        count.text = model.count
    }
}