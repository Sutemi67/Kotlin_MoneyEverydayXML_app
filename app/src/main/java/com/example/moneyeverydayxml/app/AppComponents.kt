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
            setTitle(model.description)
            if (!model.description.lowercase().contains("сброс")) {
                setMessage("Выберите действие для транзакции")
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