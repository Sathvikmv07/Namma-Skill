package com.nammaskill.app.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nammaskill.app.R
import com.nammaskill.app.data.model.Course
import com.nammaskill.app.databinding.FragmentCourseDetailBinding
import com.nammaskill.app.util.PreferencesHelper

class CourseDetailFragment : Fragment() {

    private var _binding: FragmentCourseDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CourseDetailViewModel by viewModels()
    private var currentCourse: Course? = null

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _binding = FragmentCourseDetailBinding.inflate(inflater, c, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val courseId = arguments?.getString("courseId") ?: return
        viewModel.loadCourse(courseId)

        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        binding.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_share) {
                currentCourse?.let { shareCourse(it) }
                true
            } else false
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.course.observe(viewLifecycleOwner) { course ->
            course ?: return@observe
            currentCourse = course
            populateUI(course)
        }

        viewModel.error.observe(viewLifecycleOwner) { err ->
            if (!err.isNullOrEmpty()) Toast.makeText(requireContext(), err, Toast.LENGTH_SHORT).show()
        }

        viewModel.callbackResult.observe(viewLifecycleOwner) { success ->
            success ?: return@observe
            val msg = if (success) "✅ Callback request sent!" else "❌ Failed to send request."
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun populateUI(course: Course) {
        binding.tvCourseTitle.text = course.title
        binding.tvCenterName.text = course.centerName
        binding.tvDistrict.text = "📍 ${course.district}"
        binding.tvDescription.text = course.description
        binding.tvEligibility.text = course.eligibility
        binding.tvTrainerName.text = course.trainerName
        binding.tvStartDate.text = "Start: ${course.startDate}"
        binding.tvDuration.text = course.durationLabel
        binding.tvSeats.text = "${course.seatsAvailable}/${course.totalSeats} seats available"

        // Seat progress bar
        binding.seatProgress.max = course.totalSeats
        binding.seatProgress.progress = course.totalSeats - course.seatsAvailable

        // Stipend
        if (course.stipend > 0) {
            binding.cardStipend.visibility = View.VISIBLE
            binding.tvStipend.text = "₹${course.stipend} / month"
        } else {
            binding.cardStipend.visibility = View.GONE
        }

        // Job guaranteed
        if (course.jobGuaranteed) {
            binding.bannerJobGuaranteed.visibility = View.VISIBLE
        } else {
            binding.bannerJobGuaranteed.visibility = View.GONE
        }

        // Trainer call button
        binding.btnCallTrainer.setOnClickListener {
            if (com.nammaskill.app.util.PermissionHelper.hasCallPermission(requireContext())) {
                makeCall(course.trainerContact)
            } else {
                com.nammaskill.app.util.PermissionHelper.requestCallPermission(requireActivity())
            }
        }

        // Apply Now
        binding.btnApplyNow.setOnClickListener {
            val bundle = Bundle().apply {
                putString("courseId", course.id)
                putString("courseName", course.title)
            }
            findNavController().navigate(R.id.action_detail_to_apply, bundle)
        }

        // Request Callback
        binding.btnRequestCallback.setOnClickListener {
            val phone = PreferencesHelper.getUserPhone(requireContext())
            val name = PreferencesHelper.getUserName(requireContext())
            if (phone.isEmpty()) {
                Toast.makeText(requireContext(), "Please update your profile with phone number", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.requestCallback(course.id, phone, name)
            }
        }

        // Coloring header based on trade
        val headerColor = getTradeColor(course.trade)
        binding.collapsingToolbar.setContentScrimColor(headerColor)
        binding.headerView.setBackgroundColor(headerColor)
    }

    private fun getTradeColor(trade: String): Int {
        val ctx = requireContext()
        return when (trade) {
            "Electrician" -> ctx.getColor(R.color.trade_electrician)
            "Welding" -> ctx.getColor(R.color.trade_welding)
            "Sewing" -> ctx.getColor(R.color.trade_sewing)
            "Mobile Repair" -> ctx.getColor(R.color.trade_mobile)
            "Coding" -> ctx.getColor(R.color.trade_coding)
            "Plumbing" -> ctx.getColor(R.color.trade_plumbing)
            "Carpentry" -> ctx.getColor(R.color.trade_carpentry)
            else -> ctx.getColor(R.color.primary)
        }
    }

    private fun makeCall(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
        try {
            startActivity(intent)
        } catch (e: Exception) {
            // Fallback to dialer if CALL_PHONE fails
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
            startActivity(dialIntent)
        }
    }

    private fun shareCourse(course: Course) {
        val text = "Check out this skill course: ${course.title} at ${course.centerName}, ${course.district}. " +
                "Starts ${course.startDate}. Apply via NammaSkill app!"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(Intent.createChooser(intent, "Share Course"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
