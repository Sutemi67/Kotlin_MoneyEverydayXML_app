package com.example.moneyeverydayxml.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
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

    companion object {

        @JvmStatic
        fun newInstance() =
            FragmentHistory().apply {
                arguments = Bundle().apply {
                }
            }
    }
}