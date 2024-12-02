package com.example.moneyeverydayxml.calculator.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.example.moneyeverydayxml.calculator.DecimalDigitsInputFilter
import com.example.moneyeverydayxml.databinding.FragmentCalculatorBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class CalculatorFragment : Fragment() {

    private lateinit var binding: FragmentCalculatorBinding
    private val vm: CalculatorViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalculatorBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.inputCount.filters = arrayOf(DecimalDigitsInputFilter(2))
        binding.today.text = vm.getTodayDate()

        vm.sumAmount.observe(viewLifecycleOwner) { sum ->
            binding.monthSummary.text = sum
        }
        vm.byDay.observe(viewLifecycleOwner) { byDay ->
            binding.perDay.text = byDay
        }
        vm.daysFromClearPassedLiveData.observe(viewLifecycleOwner) { date ->
            binding.daysPassed.text = date
        }
        setOnClickListeners()
        vm.getDaysFromClear()
    }

    private fun setOnClickListeners() {
        binding.increaseButton.setOnClickListener {
            if (!binding.inputCount.text.isNullOrEmpty()) {
                val input = binding.inputCount.text.toString().toBigDecimal()
                vm.increaseAction(input)
//                hideKeyboard()
                binding.inputCount.setText("")
            }
        }
        binding.decreaseButton.setOnClickListener {
            if (!binding.inputCount.text.isNullOrEmpty()) {
                val input = binding.inputCount.text.toString().toBigDecimal()
                vm.decreaseAction(input)
//                hideKeyboard()
                binding.inputCount.setText("")
            }
        }
        binding.clearButton.setOnClickListener {
            vm.clearAction()
        }

    }

    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.inputCount.windowToken, 0)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            CalculatorFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}