package com.example.moneyeverydayxml.patterns.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.moneyeverydayxml.R
import com.example.moneyeverydayxml.databinding.DialogAddPatternBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AddPatternDialogFragment : DialogFragment() {

    private var _binding: DialogAddPatternBinding? = null
    private val binding get() = _binding!!
    
    private var onPatternAddedListener: ((String, Boolean) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddPatternBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        binding.buttonAddPattern.setOnClickListener {
            val keywords = binding.editTextPattern.text.toString().trim()
            val isIncome = binding.radioGroupType.checkedRadioButtonId == binding.radioButtonIncome.id

            when {
                keywords.isEmpty() -> {
                    binding.editTextPattern.error = getString(R.string.enter_keywords)
                    return@setOnClickListener
                }
                else -> {
                    onPatternAddedListener?.invoke(keywords, isIncome)
                    dismiss()
                }
            }
        }

        binding.buttonCancel.setOnClickListener {
            dismiss()
        }
    }

    fun setOnPatternAddedListener(listener: (String, Boolean) -> Unit) {
        onPatternAddedListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 