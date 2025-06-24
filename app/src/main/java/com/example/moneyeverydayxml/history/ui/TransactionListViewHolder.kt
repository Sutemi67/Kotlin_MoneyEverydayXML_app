package com.example.moneyeverydayxml.history.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.moneyeverydayxml.R
import com.example.moneyeverydayxml.core.domain.model.Transaction

class TransactionListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val date: TextView = itemView.findViewById<TextView>(R.id.transaction_date)
    private val count: TextView = itemView.findViewById<TextView>(R.id.transaction_count)
    val imageView: ImageView = itemView.findViewById<ImageView>(R.id.imageView)

    fun bind(model: Transaction) {
        date.text = model.date
        count.text = formatTransactionText(model.count)
        setupTooltip(model.description)
    }

    private fun formatTransactionText(transactionText: String): String {
        return when {
            transactionText.contains(" - ") -> {
                val parts = transactionText.split(" - ", limit = 2)
                if (parts.size == 2) {
                    parts[0]
                } else {
                    transactionText
                }
            }

            else -> transactionText
        }
    }

    private fun setupTooltip(descriptionText: String) {
        itemView.setOnLongClickListener {
            Toast.makeText(
                itemView.context,
                descriptionText,
                Toast.LENGTH_LONG
            ).show()
            true
        }
    }
}