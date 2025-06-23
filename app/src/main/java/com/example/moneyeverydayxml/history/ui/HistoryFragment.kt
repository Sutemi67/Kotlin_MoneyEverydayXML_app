package com.example.moneyeverydayxml.history.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneyeverydayxml.app.MainViewModel
import com.example.moneyeverydayxml.databinding.FragmentHistoryBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView

class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private val vm: HistoryViewModel by viewModel()
    private val mainViewModel: MainViewModel by activityViewModels()
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

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    binding.recyclerView.scrollToPosition(0)
                }
            }
        })

        vm.transactions.observe(viewLifecycleOwner) { adapter.setData(it) }

        mainViewModel.historyDataUpdated.observe(viewLifecycleOwner) { updated ->
            if (updated) {
                vm.loadTransactions()
            }
        }
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