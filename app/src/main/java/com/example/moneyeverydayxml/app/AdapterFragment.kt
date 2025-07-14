package com.example.moneyeverydayxml.app

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.moneyeverydayxml.calculator.ui.CalculatorFragment
import com.example.moneyeverydayxml.history.ui.HistoryFragment
import com.example.moneyeverydayxml.patterns.ui.PatternsFragment

class AdapterFragment(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CalculatorFragment.Companion.newInstance()
            1 -> HistoryFragment.Companion.newInstance()
            else -> PatternsFragment.newInstance()
        }
    }
}