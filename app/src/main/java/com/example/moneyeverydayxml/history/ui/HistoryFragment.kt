package com.example.moneyeverydayxml.history.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneyeverydayxml.databinding.FragmentHistoryBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private val vm: HistoryViewModel by viewModel()
    private val adapter = TransactionListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        vm.transactions.observe(viewLifecycleOwner) { adapter.setData(it) }
    }

    override fun onResume() {
        super.onResume()
        vm.loadTransactions()
    }

    companion object {
        @JvmStatic
        fun newInstance() = HistoryFragment().apply { arguments = Bundle().apply {} }
    }
}