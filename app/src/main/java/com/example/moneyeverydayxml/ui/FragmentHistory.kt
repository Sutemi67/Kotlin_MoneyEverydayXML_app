package com.example.moneyeverydayxml.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.moneyeverydayxml.databinding.FragmentHistoryBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentHistory : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private val vm: HistoryViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.dates.observe(viewLifecycleOwner) { dates ->
            binding.date1.text = dates[0]
            binding.date2.text = dates[1]
            binding.date3.text = dates[2]
            binding.date4.text = dates[3]
            binding.date5.text = dates[4]
        }
        vm.counts.observe(viewLifecycleOwner) { counts ->

            binding.count1.text = counts[0]
            binding.count2.text = counts[1]
            binding.count3.text = counts[2]
            binding.count4.text = counts[3]
            binding.count5.text = counts[4]
        }
        vm.loadData()
        vm.loadDates()
        vm.loadCounts()
    }

    override fun onResume() {
        super.onResume()
        vm.loadData()
        vm.loadDates()
        vm.loadCounts()
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            FragmentHistory().apply {
                arguments = Bundle().apply {
                }
            }
    }
}