package com.example.moneyeverydayxml.calculator.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.moneyeverydayxml.R
import com.example.moneyeverydayxml.app.MainViewModel
import com.example.moneyeverydayxml.calculator.DecimalDigitsInputFilter
import com.example.moneyeverydayxml.databinding.FragmentCalculatorBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.math.BigDecimal
import kotlinx.coroutines.launch

class CalculatorFragment : Fragment() {

    private lateinit var binding: FragmentCalculatorBinding
    private val vm: CalculatorViewModel by viewModel()
    private val mainViewModel: MainViewModel by activityViewModels()

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
        binding.today.text = vm.currentTimeFormattedString()

        vm.sumAmount.observe(viewLifecycleOwner) { sum ->
            binding.monthSummary.text = sum
            val sumBigDecimal = sum.toBigDecimalOrNull() ?: BigDecimal.ZERO
            if (sumBigDecimal < BigDecimal(0)) {
                binding.monthSummary.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.summary_negative
                    )
                )
            } else {
                binding.monthSummary.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.summary_positive
                    )
                )
            }
        }

        vm.byDay.observe(viewLifecycleOwner) { byDay ->
            binding.perDay.text = byDay
            val sumBigDecimal = byDay.toBigDecimalOrNull() ?: BigDecimal.ZERO
            if (sumBigDecimal < BigDecimal(0)) {
                binding.perDay.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.summary_negative
                    )
                )
            } else {
                binding.perDay.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.summary_positive
                    )
                )
            }
        }
        vm.daysFromClearPassedLiveData.observe(viewLifecycleOwner) { date ->
            binding.daysPassed.text = date
        }
        setOnClickListeners()
        vm.getDaysFromClear()

        mainViewModel.calculatorDataUpdated.observe(viewLifecycleOwner) { updated ->
            if (updated) {
                viewLifecycleOwner.lifecycleScope.launch {
                    vm.refreshData()
                }
                mainViewModel.onCalculatorDataUpdated()
            }
        }
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

    override fun onResume() {
        super.onResume()
        viewLifecycleOwner.lifecycleScope.launch {
            vm.refreshData()
        }
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