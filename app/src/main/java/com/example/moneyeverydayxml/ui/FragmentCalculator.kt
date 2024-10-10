package com.example.moneyeverydayxml.ui

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.moneyeverydayxml.databinding.FragmentCalculatorBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentCalculator : Fragment() {

    private lateinit var binding: FragmentCalculatorBinding
    private val vm by viewModel<CalculatorViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalculatorBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()

        binding.today.text = vm.getTodayAndData()
        binding.daysPassed.text = vm.getDaysFromClear()
        binding.inputCount.setInputType(InputType.TYPE_CLASS_NUMBER)

        vm.sumAmount.observe(viewLifecycleOwner) { sum ->
            binding.monthSummary.text = sum
        }
        vm.byDay.observe(viewLifecycleOwner) { byDay ->
            binding.perDay.text = byDay
        }
        vm.daysFromClearPassedLiveData.observe(viewLifecycleOwner) { date ->
            binding.daysPassed.text = date
        }
    }

    private fun setOnClickListeners() {
        binding.increaseButton.setOnClickListener {
            if (!binding.inputCount.text.isNullOrEmpty()) {
                val input = binding.inputCount.text.toString().toInt()
                vm.increaseAction(input)
            }
        }
        binding.decreaseButton.setOnClickListener {
            if (!binding.inputCount.text.isNullOrEmpty()) {
                val input = binding.inputCount.text.toString().toInt()
                vm.decreaseAction(input)
            }
        }
        binding.clearButton.setOnClickListener {
            vm.clearAction()
        }
    }


    companion object {

        @JvmStatic
        fun newInstance() =
            FragmentCalculator().apply {
                arguments = Bundle().apply {
                }
            }
    }
}