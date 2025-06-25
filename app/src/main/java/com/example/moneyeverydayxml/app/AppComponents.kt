package com.example.moneyeverydayxml.app

import android.app.AlertDialog
import android.view.View
import com.example.moneyeverydayxml.R
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
    
    fun aboutAppDialog(context: android.content.Context) {
        AlertDialog.Builder(context).apply {
            setTitle(context.getString(R.string.about_app_title))
            setMessage(context.getString(R.string.about_app_message))
            setPositiveButton(context.getString(R.string.about_app_ok_button)) { dialog, _ ->
                dialog.dismiss()
            }
        }.create().show()
    }
}