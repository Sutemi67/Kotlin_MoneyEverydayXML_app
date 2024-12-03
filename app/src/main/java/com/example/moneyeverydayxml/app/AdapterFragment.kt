package com.example.moneyeverydayxml.app

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.moneyeverydayxml.calculator.ui.CalculatorFragment
import com.example.moneyeverydayxml.history.ui.HistoryFragment

class AdapterFragment(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CalculatorFragment.Companion.newInstance()
            else -> HistoryFragment.Companion.newInstance()
        }
    }
}