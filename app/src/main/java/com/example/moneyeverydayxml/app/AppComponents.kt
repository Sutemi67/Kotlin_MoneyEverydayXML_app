package com.example.moneyeverydayxml.app

import android.app.AlertDialog
import android.view.View
import com.example.moneyeverydayxml.core.domain.model.Transaction

object AppComponents {
    fun transactionDialog(
        itemView: View,
        model: Transaction,
        onDelete: () -> Unit,
        onEdit: () -> Unit
    ) {
        AlertDialog.Builder(itemView.context).apply {
            setTitle("Выберите действие для транзакции")
            if (!model.description.lowercase().contains("сброс")) {
                setMessage(model.description)
                setPositiveButton("Удалить") { _, _ ->
                    onDelete()
                }
                setNegativeButton("Изменить") { _, _ ->
                    onEdit()
                }
            }
            setNeutralButton("Закрыть") { dialog, _ ->
                dialog.cancel()
            }
        }.create().show()
    }
}