package com.example.moneyeverydayxml.history.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.ListAdapter
import com.example.moneyeverydayxml.R
import com.example.moneyeverydayxml.history.domain.model.Transaction

class TransactionListAdapter :
    ListAdapter<Transaction, TransactionListViewHolder>(DiffUtilCallback()) {
    private val difUtil = DiffUtilCallback()
    private val asyncListDiffer = AsyncListDiffer(this, difUtil)

    fun setData(list: List<Transaction>) {
        asyncListDiffer.submitList(list.toList())
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_element, parent, false)
        return TransactionListViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: TransactionListViewHolder,
        position: Int
    ) {
        val item = asyncListDiffer.currentList[position]
        holder.bind(item)
        val colorRes = when {
            item.count.contains("-") -> {
                R.color.summary_negative
            }

            else -> {
                R.color.summary_positive
            }
        }
        holder.imageView.setColorFilter(
            ContextCompat.getColor(holder.itemView.context, colorRes)
        )
    }

    override fun getItemCount(): Int = asyncListDiffer.currentList.size

}