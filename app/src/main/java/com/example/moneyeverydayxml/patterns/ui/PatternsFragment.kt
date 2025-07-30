package com.example.moneyeverydayxml.patterns.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneyeverydayxml.R
import com.example.moneyeverydayxml.app.AppComponents
import com.example.moneyeverydayxml.databinding.FragmentPatternsBinding
import com.example.moneyeverydayxml.patterns.ui.adapter.PatternsAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class PatternsFragment : Fragment() {

    private var _binding: FragmentPatternsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PatternsViewModel by viewModel()
    private lateinit var adapter: PatternsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPatternsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupFab()
    }

    private fun setupRecyclerView() {
        adapter = PatternsAdapter(
            onPatternDelete = { pattern ->
                showDeleteConfirmationDialog(pattern)
            }
        )
        binding.recyclerViewPatterns.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@PatternsFragment.adapter
        }
    }

    private fun setupObservers() {
        viewModel.patterns.observe(viewLifecycleOwner) { patterns ->
            if (patterns.isEmpty()) {
                binding.emptyStateLayout.visibility = View.VISIBLE
                binding.recyclerViewPatterns.visibility = View.GONE
            } else {
                binding.emptyStateLayout.visibility = View.GONE
                binding.recyclerViewPatterns.visibility = View.VISIBLE
                adapter.submitList(patterns)
            }
        }
    }

    private fun setupFab() {
        binding.fabAddPattern.setOnClickListener {
            showAddPatternDialog()
        }
    }

    private fun showAddPatternDialog() {
        AppComponents.addPatternDialog(requireContext()) { keywords, isIncome ->
            viewModel.addPattern(keywords, isIncome)
            Snackbar.make(binding.root, getString(R.string.pattern_added), Snackbar.LENGTH_SHORT)
                .show()
        }
    }

    private fun showDeleteConfirmationDialog(pattern: com.example.moneyeverydayxml.core.domain.model.NotificationPattern) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_pattern))
            .setMessage("${getString(R.string.delete_pattern_confirmation)} \"${pattern.keywords}\"?")
            .setPositiveButton(getString(R.string.delete_pattern)) { _, _ ->
                viewModel.deletePattern(pattern)
                Snackbar.make(
                    binding.root,
                    getString(R.string.pattern_deleted),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton(getString(android.R.string.cancel), null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): PatternsFragment {
            return PatternsFragment()
        }
    }
} 