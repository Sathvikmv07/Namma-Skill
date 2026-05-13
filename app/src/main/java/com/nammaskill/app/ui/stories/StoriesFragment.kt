package com.nammaskill.app.ui.stories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.nammaskill.app.databinding.FragmentStoriesBinding

class StoriesFragment : Fragment() {

    private var _binding: FragmentStoriesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StoriesViewModel by viewModels()
    private lateinit var adapter: StoriesAdapter

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _binding = FragmentStoriesBinding.inflate(inflater, c, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = StoriesAdapter()
        binding.rvStories.layoutManager = LinearLayoutManager(requireContext())
        binding.rvStories.adapter = adapter

        setupFilterChips()
        animateStatCounters()
        observeViewModel()
    }

    private fun setupFilterChips() {
        val districts = listOf("All","Dharwad","Davangere","Hubli","Belgaum","Mysore","Hassan")
        districts.forEachIndexed { i, d ->
            val chip = Chip(requireContext()).apply {
                text = d; isCheckable = true; isChecked = i == 0; id = View.generateViewId()
            }
            binding.chipGroupDistrict.addView(chip)
        }
        binding.chipGroupDistrict.setOnCheckedStateChangeListener { group, ids ->
            if (ids.isEmpty()) return@setOnCheckedStateChangeListener
            val chip = group.findViewById<Chip>(ids[0])
            viewModel.filterByDistrict(chip?.text?.toString() ?: "All")
        }

        val trades = listOf("All","Electrician","Welding","Sewing","Mobile Repair","Coding","Plumbing")
        trades.forEachIndexed { i, t ->
            val chip = Chip(requireContext()).apply {
                text = t; isCheckable = true; isChecked = i == 0; id = View.generateViewId()
            }
            binding.chipGroupTrade.addView(chip)
        }
        binding.chipGroupTrade.setOnCheckedStateChangeListener { group, ids ->
            if (ids.isEmpty()) return@setOnCheckedStateChangeListener
            val chip = group.findViewById<Chip>(ids[0])
            viewModel.filterByTrade(chip?.text?.toString() ?: "All")
        }
    }

    private fun animateStatCounters() {
        animateCounter(binding.tvStatTrained, 0, 2847, "Youth Trained", 1500)
        animateCounter(binding.tvStatJobs, 0, 1923, "Got Jobs", 1500)
        animateCounter(binding.tvStatRate, 0, 68, "% Placement", 1500)
    }

    private fun animateCounter(tv: TextView, from: Int, to: Int, suffix: String, duration: Long) {
        val steps = 60
        val stepDelay = duration / steps
        val increment = (to - from).toFloat() / steps
        var current = from.toFloat()
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                current += increment
                if (current >= to) {
                    tv.text = "$to\n$suffix"
                } else {
                    tv.text = "${current.toInt()}\n$suffix"
                    handler.postDelayed(this, stepDelay)
                }
            }
        }
        handler.postDelayed(runnable, 500)
    }

    private fun observeViewModel() {
        viewModel.filteredStories.observe(viewLifecycleOwner) { stories ->
            adapter.submitList(stories)
            binding.emptyState.visibility = if (stories.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
