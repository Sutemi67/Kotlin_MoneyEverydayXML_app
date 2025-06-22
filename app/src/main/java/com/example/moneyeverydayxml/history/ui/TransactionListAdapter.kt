package com.example.moneyeverydayxml.history.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.ListAdapter
import com.example.moneyeverydayxml.R
import com.example.moneyeverydayxml.history.domain.model.Transaction
import java.math.BigDecimal

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
        
        // Определяем цвет иконки на основе типа транзакции
        val colorRes = when {
            // Тестовые транзакции
            item.count.contains("[ТЕСТ]") -> {
                if (isTestTransactionNegative(item.count)) {
                    R.color.summary_negative
                } else {
                    R.color.summary_positive
                }
            }
            // Обычные транзакции
            else -> {
                val amount = extractAmountFromTransaction(item.count)
                if (amount < 0) {
                    R.color.summary_negative
                } else {
                    R.color.summary_positive
                }
            }
        }
        
        holder.imageView.setColorFilter(
            ContextCompat.getColor(holder.itemView.context, colorRes)
        )
    }

    override fun getItemCount(): Int = asyncListDiffer.currentList.size
    
    /**
     * Извлекает сумму из строки транзакции
     */
    private fun extractAmountFromTransaction(transactionText: String): Int {
        return try {
            // Пытаемся извлечь число из начала строки
            val numberMatch = Regex("^([+-]?\\d+(\\.\\d+)?)").find(transactionText)
            numberMatch?.value?.toBigDecimalOrNull()?.toInt() ?: 0
        } catch (e: Exception) {
            0
        }
    }
    
    /**
     * Проверяет, является ли тестовая транзакция отрицательной
     */
    private fun isTestTransactionNegative(transactionText: String): Boolean {
        return transactionText.contains("-") && !transactionText.startsWith("+")
    }
}