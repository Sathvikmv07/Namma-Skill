package com.nammaskill.app.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nammaskill.app.R
import com.nammaskill.app.databinding.FragmentProfileBinding
import com.nammaskill.app.service.NammaSkillMessagingService
import com.nammaskill.app.util.PreferencesHelper

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var appAdapter: ApplicationAdapter

    private val allTrades = listOf("Electrician","Welding","Sewing","Mobile Repair","Coding","Plumbing","Carpentry")

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, c, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupApplicationsRecyclerView()
        loadProfile()
        setupEditButton()
        setupTradeChips()
        setupNotificationToggle()
        setupFeedbackCard()

        viewModel.applications.observe(viewLifecycleOwner) { apps ->
            appAdapter.submitList(apps)
            binding.tvNoApplications.visibility = if (apps.isEmpty()) View.VISIBLE else View.GONE
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.applicationsProgress.visibility = if (loading) View.VISIBLE else View.GONE
        }
    }

    private fun setupApplicationsRecyclerView() {
        appAdapter = ApplicationAdapter()
        binding.rvApplications.adapter = appAdapter
        binding.rvApplications.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(requireContext())
    }

    private fun loadProfile() {
        val ctx = requireContext()
        val name = PreferencesHelper.getUserName(ctx)
        val village = PreferencesHelper.getUserVillage(ctx)
        val district = PreferencesHelper.getUserDistrict(ctx)
        val phone = PreferencesHelper.getUserPhone(ctx)

        binding.tvUserName.text = name.ifEmpty { "Your Name" }
        binding.tvUserLocation.text = if (village.isNotEmpty() && district.isNotEmpty()) "$village, $district"
            else district.ifEmpty { "Location not set" }

        val initials = name.split(" ").take(2).mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("")
        binding.tvAvatar.text = initials.ifEmpty { "?" }

        viewModel.loadApplications(phone)
    }

    private fun setupEditButton() {
        binding.btnEdit.setOnClickListener { showEditDialog() }
    }

    private fun showEditDialog() {
        val ctx = requireContext()
        val dialogView = LayoutInflater.from(ctx).inflate(R.layout.dialog_edit_profile, null)
        val etName = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEditName)
        val etPhone = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEditPhone)
        val etVillage = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEditVillage)
        val spinnerDistrict = dialogView.findViewById<android.widget.Spinner>(R.id.spinnerEditDistrict)
        val districts = listOf("Bagalkot","Bangalore Rural","Bangalore Urban","Belagavi","Bellary",
            "Bidar","Chamarajanagar","Chikkaballapur","Chikkamagaluru","Chitradurga",
            "Dakshina Kannada","Davangere","Dharwad","Gadag","Hassan","Haveri","Kalaburagi",
            "Kodagu","Kolar","Koppal","Mandya","Mysore","Raichur","Ramanagara","Shimoga",
            "Tumkur","Udupi","Uttara Kannada","Vijayapura","Yadgir","Hubli","Belgaum","Mangalore").sorted()
        spinnerDistrict.adapter = ArrayAdapter(ctx, android.R.layout.simple_spinner_item, districts)
            .also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        etName.setText(PreferencesHelper.getUserName(ctx))
        etPhone.setText(PreferencesHelper.getUserPhone(ctx))
        etVillage.setText(PreferencesHelper.getUserVillage(ctx))
        val savedDistrict = PreferencesHelper.getUserDistrict(ctx)
        spinnerDistrict.setSelection(districts.indexOf(savedDistrict).coerceAtLeast(0))

        MaterialAlertDialogBuilder(ctx)
            .setTitle("Edit Profile")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                PreferencesHelper.setUserName(ctx, etName.text.toString())
                PreferencesHelper.setUserPhone(ctx, etPhone.text.toString())
                PreferencesHelper.setUserVillage(ctx, etVillage.text.toString())
                PreferencesHelper.setUserDistrict(ctx, districts[spinnerDistrict.selectedItemPosition])
                loadProfile()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setupTradeChips() {
        val ctx = requireContext()
        val preferred = PreferencesHelper.getPreferredTrades(ctx).toMutableSet()
        binding.chipGroupTrades.removeAllViews()

        allTrades.forEach { trade ->
            val chip = Chip(ctx).apply {
                text = trade
                isCheckable = true
                isChecked = preferred.contains(trade)
                id = View.generateViewId()
                setOnCheckedChangeListener { _, checked ->
                    if (checked) {
                        preferred.add(trade)
                        PreferencesHelper.subscribeToTrade(trade)
                    } else {
                        preferred.remove(trade)
                        PreferencesHelper.unsubscribeFromTrade(trade)
                    }
                    PreferencesHelper.setPreferredTrades(ctx, preferred)
                }
            }
            binding.chipGroupTrades.addView(chip)
        }
    }

    private fun setupNotificationToggle() {
        binding.switchNotifications.isChecked = true // Default to true
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Logic to enable FCM or local prefs
            } else {
                // Logic to disable
            }
        }
    }

    private fun setupFeedbackCard() {
        binding.cardFeedback.setOnClickListener { showFeedbackDialog() }
    }

    private fun showFeedbackDialog() {
        val ctx = requireContext()
        val etFeedback = com.google.android.material.textfield.TextInputEditText(ctx).apply {
            hint = "Share your thoughts..."
            minLines = 3
        }
        val layout = android.widget.FrameLayout(ctx).apply {
            val pad = (16 * ctx.resources.displayMetrics.density).toInt()
            setPadding(pad, pad, pad, pad)
            addView(etFeedback)
        }

        MaterialAlertDialogBuilder(ctx)
            .setTitle("Send Feedback")
            .setView(layout)
            .setPositiveButton("Send") { _, _ ->
                val feedbackText = etFeedback.text.toString()
                if (feedbackText.isNotEmpty()) {
                    // Logic to send to Firestore would go here
                    android.widget.Toast.makeText(ctx, "Thank you for your feedback!", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
