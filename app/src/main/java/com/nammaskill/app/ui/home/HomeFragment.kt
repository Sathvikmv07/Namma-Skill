package com.nammaskill.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.nammaskill.app.R
import com.nammaskill.app.databinding.FragmentHomeBinding
import com.nammaskill.app.util.PreferencesHelper

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var courseAdapter: CourseAdapter

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, c, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUserLocation()
        setupRecyclerView()
        setupTradeChips()
        setupDurationChips()
        setupJobGuaranteedToggle()
        setupSearch()
        observeViewModel()
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                viewModel.setSearchQuery(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {}
        })
    }

    private fun setupUserLocation() {
        val district = PreferencesHelper.getUserDistrict(requireContext())
        binding.tvLocation.text = if (district.isNotEmpty()) "📍 $district" else "📍 Karnataka"
    }

    private fun setupRecyclerView() {
        courseAdapter = CourseAdapter(
            onApply = { course ->
                val bundle = Bundle().apply {
                    putString("courseId", course.id)
                    putString("courseName", course.title)
                }
                findNavController().navigate(R.id.action_home_to_apply, bundle)
            },
            onInterested = { course ->
                // Save interest via ViewModel
            },
            onCardClick = { course ->
                val bundle = Bundle().apply { putString("courseId", course.id) }
                findNavController().navigate(R.id.action_home_to_detail, bundle)
            }
        )
        binding.rvCourses.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = courseAdapter
        }
    }

    private fun setupTradeChips() {
        val trades = listOf("All", "Electrician", "Welding", "Sewing", "Mobile Repair", "Coding", "Plumbing", "Carpentry")
        trades.forEachIndexed { index, trade ->
            val chip = Chip(requireContext()).apply {
                text = trade
                isCheckable = true
                isChecked = index == 0
                id = View.generateViewId()
                setChipBackgroundColorResource(R.color.chip_state_list)
                setTextColor(resources.getColorStateList(R.color.chip_text_state_list, null))
            }
            binding.chipGroupTrade.addView(chip)
        }
        binding.chipGroupTrade.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener
            val chip = group.findViewById<Chip>(checkedIds[0])
            viewModel.filterByTrade(chip?.text?.toString() ?: "All")
        }
    }

    private fun setupDurationChips() {
        val durations = listOf("All" to "All", "Short (<3 months)" to "short",
            "Long (3–6 months)" to "long", "Advanced (6+)" to "advanced")
        durations.forEachIndexed { index, (label, value) ->
            val chip = Chip(requireContext()).apply {
                text = label
                isCheckable = true
                isChecked = index == 0
                id = View.generateViewId()
                tag = value
                setChipBackgroundColorResource(R.color.chip_state_list)
                setTextColor(resources.getColorStateList(R.color.chip_text_state_list, null))
            }
            binding.chipGroupDuration.addView(chip)
        }
        binding.chipGroupDuration.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener
            val chip = group.findViewById<Chip>(checkedIds[0])
            viewModel.filterByDuration(chip?.tag?.toString() ?: "All")
        }
    }

    private fun setupJobGuaranteedToggle() {
        binding.switchJobGuaranteed.setOnCheckedChangeListener { _, isChecked ->
            viewModel.filterByJobGuaranteed(isChecked)
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.shimmerLayout.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.rvCourses.visibility = if (isLoading) View.GONE else View.VISIBLE
            if (isLoading) binding.shimmerLayout.startShimmer()
            else binding.shimmerLayout.stopShimmer()
        }

        viewModel.filteredCourses.observe(viewLifecycleOwner) { courses ->
            courseAdapter.submitList(courses)
            val isEmpty = courses.isEmpty()
            binding.emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
            binding.rvCourses.visibility = if (isEmpty) View.GONE else View.VISIBLE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                com.google.android.material.snackbar.Snackbar.make(binding.root, error, com.google.android.material.snackbar.Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
