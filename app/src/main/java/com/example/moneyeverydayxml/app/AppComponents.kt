package com.example.moneyeverydayxml.app

import android.app.AlertDialog
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import com.example.moneyeverydayxml.R
import com.example.moneyeverydayxml.core.domain.model.Transaction
import com.google.android.material.dialog.MaterialAlertDialogBuilder

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

    fun addPatternDialog(
        context: android.content.Context,
        onPatternAdded: (String, Boolean) -> Unit
    ) {
        val dialog = MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.add_pattern))
            .setView(R.layout.dialog_add_pattern_content)
            .setPositiveButton(context.getString(R.string.add_pattern), null) // Устанавливаем null, чтобы предотвратить закрытие
            .setNegativeButton(android.R.string.cancel, null)
            .create()

        dialog.setOnShowListener {
            val editText = dialog.findViewById<EditText>(R.id.editTextPattern)
            val radioGroup = dialog.findViewById<RadioGroup>(R.id.radioGroupType)
            val radioButtonIncome = dialog.findViewById<RadioButton>(R.id.radioButtonIncome)
            
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                val keywords = editText?.text?.toString()?.trim() ?: ""
                val isIncome = radioGroup?.checkedRadioButtonId == radioButtonIncome?.id
                
                if (keywords.isNotEmpty()) {
                    onPatternAdded(keywords, isIncome)
                    dialog.dismiss()
                } else {
                    editText?.error = context.getString(R.string.enter_keywords)
                }
            }
        }
        
        dialog.show()
    }
}