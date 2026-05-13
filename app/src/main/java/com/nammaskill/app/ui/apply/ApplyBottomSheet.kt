package com.nammaskill.app.ui.apply

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nammaskill.app.R
import com.nammaskill.app.databinding.FragmentApplyBottomSheetBinding
import com.nammaskill.app.util.PreferencesHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ApplyBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentApplyBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ApplyViewModel by viewModels()

    private val districts = listOf("Bagalkot","Bangalore Rural","Bangalore Urban","Belagavi","Bellary",
        "Bidar","Chamarajanagar","Chikkaballapur","Chikkamagaluru","Chitradurga",
        "Dakshina Kannada","Davangere","Dharwad","Gadag","Hassan","Haveri","Kalaburagi",
        "Kodagu","Kolar","Koppal","Mandya","Mysore","Raichur","Ramanagara","Shimoga",
        "Tumkur","Udupi","Uttara Kannada","Vijayapura","Yadgir","Hubli","Belgaum","Mangalore").sorted()

    private val educationOptions = listOf("8th Pass", "10th Pass", "12th Pass", "Graduate", "Post Graduate")
    private val languageOptions = listOf("Kannada", "Hindi", "English")

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _binding = FragmentApplyBottomSheetBinding.inflate(inflater, c, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val courseId = arguments?.getString("courseId") ?: ""
        val courseName = arguments?.getString("courseName") ?: ""
        binding.tvApplyingFor.text = "Applying for: $courseName"

        setupDropdowns()
        autoFillProfile()
        setupLivePreview(courseName)
        setupSubmitButton(courseId, courseName)

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.btnSubmit.isEnabled = !loading
            binding.btnSubmit.text = if (loading) "Submitting..." else "Submit Application"
        }

        viewModel.submissionResult.observe(viewLifecycleOwner) { result ->
            result ?: return@observe
            if (result.isSuccess) {
                showSuccessDialog(courseName)
            } else {
                Toast.makeText(requireContext(), "❌ Submission failed. Please try again.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupDropdowns() {
        val distAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, districts)
        binding.spinnerDistrict.adapter = distAdapter

        val eduAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, educationOptions)
        binding.spinnerEducation.adapter = eduAdapter

        val langAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, languageOptions)
        binding.spinnerLanguage.adapter = langAdapter
    }

    private fun autoFillProfile() {
        val ctx = requireContext()
        binding.etName.setText(PreferencesHelper.getUserName(ctx))
        binding.etPhone.setText(PreferencesHelper.getUserPhone(ctx))
        binding.etVillage.setText(PreferencesHelper.getUserVillage(ctx))
        val savedDistrict = PreferencesHelper.getUserDistrict(ctx)
        val idx = districts.indexOf(savedDistrict)
        if (idx >= 0) binding.spinnerDistrict.setSelection(idx)
        val savedEdu = PreferencesHelper.getUserEducation(ctx)
        val eduIdx = educationOptions.indexOf(savedEdu)
        if (eduIdx >= 0) binding.spinnerEducation.setSelection(eduIdx)
    }

    private fun setupLivePreview(courseName: String) {
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = updatePreview(courseName)
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {}
        }
        binding.etName.addTextChangedListener(watcher)
        binding.etAge.addTextChangedListener(watcher)
        binding.etPhone.addTextChangedListener(watcher)
        binding.etVillage.addTextChangedListener(watcher)
        binding.etExperience.addTextChangedListener(watcher)
    }

    private fun updatePreview(courseName: String) {
        val today = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        val preview = """
--- CANDIDATE SUMMARY ---
Name       : ${binding.etName.text}
Age        : ${binding.etAge.text} years
Contact    : +91 ${binding.etPhone.text}
Location   : ${binding.etVillage.text}, ${districts.getOrElse(binding.spinnerDistrict.selectedItemPosition) { "" }}
Education  : ${educationOptions.getOrElse(binding.spinnerEducation.selectedItemPosition) { "" }}
Applying For: $courseName
Experience : ${binding.etExperience.text.toString().ifEmpty { "Fresher" }}
Language   : ${languageOptions.getOrElse(binding.spinnerLanguage.selectedItemPosition) { "Kannada" }}
Date       : $today
--------------------------""".trimIndent()
        binding.tvSummaryPreview.text = preview
    }

    private fun setupSubmitButton(courseId: String, courseName: String) {
        binding.btnSubmit.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val age = binding.etAge.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val village = binding.etVillage.text.toString().trim()

            if (name.isEmpty() || age.isEmpty() || phone.isEmpty() || village.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all required fields (*)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (phone.length != 10) {
                Toast.makeText(requireContext(), "Enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save profile
            PreferencesHelper.setUserName(requireContext(), name)
            PreferencesHelper.setUserPhone(requireContext(), phone)
            PreferencesHelper.setUserVillage(requireContext(), village)
            PreferencesHelper.setUserEducation(requireContext(), educationOptions.getOrElse(binding.spinnerEducation.selectedItemPosition) { "" })

            viewModel.submitApplication(
                courseId = courseId,
                courseName = courseName,
                name = name,
                age = age,
                phone = phone,
                village = village,
                district = districts.getOrElse(binding.spinnerDistrict.selectedItemPosition) { "" },
                education = educationOptions.getOrElse(binding.spinnerEducation.selectedItemPosition) { "" },
                experience = binding.etExperience.text.toString().trim(),
                language = languageOptions.getOrElse(binding.spinnerLanguage.selectedItemPosition) { "Kannada" }
            )
        }
    }

    private fun showSuccessDialog(courseName: String) {
        val summary = binding.tvSummaryPreview.text.toString()
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("🎉 Application Submitted!")
            .setMessage(summary)
            .setPositiveButton("Copy Summary") { _, _ ->
                val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("Application Summary", summary))
                Toast.makeText(requireContext(), "Summary copied!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Done") { _, _ -> dismiss() }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(courseId: String, courseName: String) = ApplyBottomSheet().apply {
            arguments = Bundle().apply {
                putString("courseId", courseId)
                putString("courseName", courseName)
            }
        }
    }
}
