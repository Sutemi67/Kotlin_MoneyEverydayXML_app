package com.example.moneyeverydayxml.patterns.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.moneyeverydayxml.R
import com.example.moneyeverydayxml.core.domain.model.NotificationPattern
import com.example.moneyeverydayxml.databinding.ItemPatternBinding

class PatternsAdapter(
    private val onPatternDelete: (NotificationPattern) -> Unit
) : ListAdapter<NotificationPattern, PatternsAdapter.PatternViewHolder>(PatternDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatternViewHolder {
        val binding = ItemPatternBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PatternViewHolder(binding, onPatternDelete)
    }

    override fun onBindViewHolder(holder: PatternViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PatternViewHolder(
        private val binding: ItemPatternBinding,
        private val onPatternDelete: (NotificationPattern) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(pattern: NotificationPattern) {
            binding.apply {
                textPatternDescription.text = pattern.keywords
                chipPatternType.text = if (pattern.isIncome) itemView.context.getString(R.string.income) else itemView.context.getString(R.string.expense)
                
                buttonDeletePattern.setOnClickListener {
                    onPatternDelete(pattern)
                }
            }
        }
    }

    private class PatternDiffCallback : DiffUtil.ItemCallback<NotificationPattern>() {
        override fun areItemsTheSame(oldItem: NotificationPattern, newItem: NotificationPattern): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NotificationPattern, newItem: NotificationPattern): Boolean {
            return oldItem == newItem
        }
    }
} 