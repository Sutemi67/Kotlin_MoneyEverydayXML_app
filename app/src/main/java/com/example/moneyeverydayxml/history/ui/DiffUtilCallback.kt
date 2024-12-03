package com.example.moneyeverydayxml.history.ui

import androidx.recyclerview.widget.DiffUtil
import com.example.moneyeverydayxml.history.domain.model.Transaction

class DiffUtilCallback : DiffUtil.ItemCallback<Transaction>() {
    override fun areItemsTheSame(
        oldItem: Transaction,
        newItem: Transaction
    ): Boolean {
        return oldItem.date == newItem.date
    }

    override fun areContentsTheSame(
        oldItem: Transaction,
        newItem: Transaction
    ): Boolean {
        return oldItem == newItem
    }
}