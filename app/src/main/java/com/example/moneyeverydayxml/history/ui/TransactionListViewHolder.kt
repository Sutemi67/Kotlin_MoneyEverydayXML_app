package com.example.moneyeverydayxml.history.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.moneyeverydayxml.R
import com.example.moneyeverydayxml.app.AppComponents
import com.example.moneyeverydayxml.core.domain.model.Transaction

class TransactionListViewHolder(
    itemView: View,
    private val onDeleteClick: (Transaction) -> Unit,
    private val onEditClick: (Transaction) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private val date: TextView = itemView.findViewById<TextView>(R.id.transaction_date)
    private val count: TextView = itemView.findViewById<TextView>(R.id.transaction_count)
    val imageView: ImageView = itemView.findViewById<ImageView>(R.id.imageView)

    fun bind(model: Transaction) {
        date.text = model.date
        count.text = formatTransactionText(model.count)
        setupDialog(model)
        imageChooser()
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

    private fun setupDialog(model: Transaction) {
        itemView.setOnClickListener {
            AppComponents.transactionDialog(
                itemView,
                model,
                onDelete = { onDeleteClick(model) },
                onEdit = { onEditClick(model) }
            )
        }
    }

    private fun imageChooser() {
        val imageRes: Pair<Int, Int> = when {
            count.text.contains("-") -> Pair(
                R.color.summary_negative,
                R.drawable.outline_keyboard_double_arrow_up_24
            )

            count.text.contains(" ") -> Pair(
                R.color.summary_neutral,
                R.drawable.outline_keyboard_double_arrow_left_24
            )

            else -> Pair(
                R.color.summary_positive,
                R.drawable.outline_keyboard_double_arrow_down_24
            )
        }
        imageView.setColorFilter(
            ContextCompat.getColor(itemView.context, imageRes.first)
        )
        imageView.setImageDrawable(
            ContextCompat.getDrawable(itemView.context, imageRes.second)
        )
    }
}