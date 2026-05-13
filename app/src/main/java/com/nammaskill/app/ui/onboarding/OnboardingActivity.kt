package com.nammaskill.app.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.nammaskill.app.R
import com.nammaskill.app.databinding.ActivityOnboardingBinding
import com.nammaskill.app.databinding.FragmentOnboardingPage1Binding
import com.nammaskill.app.databinding.FragmentOnboardingPage2Binding
import com.nammaskill.app.databinding.FragmentOnboardingPage3Binding
import com.nammaskill.app.ui.main.MainActivity
import com.nammaskill.app.util.PreferencesHelper

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPager.adapter = OnboardingPagerAdapter(this)

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }.attach()

        binding.btnSkip.setOnClickListener { finishOnboarding("Karnataka") }
        binding.btnNext.setOnClickListener {
            val current = binding.viewPager.currentItem
            if (current < 2) {
                binding.viewPager.currentItem = current + 1
            }
        }

        binding.viewPager.registerOnPageChangeCallback(object :
            androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == 2) {
                    binding.btnNext.visibility = View.GONE
                } else {
                    binding.btnNext.visibility = View.VISIBLE
                }
            }
        })
    }

    fun finishOnboarding(district: String) {
        PreferencesHelper.setUserDistrict(this, district)
        PreferencesHelper.setOnboardingDone(this, true)
        startActivity(Intent(this, MainActivity::class.java))
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        finish()
    }
}

class OnboardingPagerAdapter(activity: OnboardingActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount() = 3
    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> OnboardingPage1Fragment()
        1 -> OnboardingPage2Fragment()
        else -> OnboardingPage3Fragment()
    }
}

class OnboardingPage1Fragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, s: Bundle?): View =
        FragmentOnboardingPage1Binding.inflate(inflater, c, false).root
}

class OnboardingPage2Fragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, s: Bundle?): View =
        FragmentOnboardingPage2Binding.inflate(inflater, c, false).root
}

class OnboardingPage3Fragment : Fragment() {
    private var _binding: FragmentOnboardingPage3Binding? = null
    private val binding get() = _binding!!

    private val districts = listOf(
        "Bagalkot", "Bangalore Rural", "Bangalore Urban", "Belagavi", "Bellary",
        "Bidar", "Chamarajanagar", "Chikkaballapur", "Chikkamagaluru", "Chitradurga",
        "Dakshina Kannada", "Davangere", "Dharwad", "Gadag", "Hassan",
        "Haveri", "Kalaburagi", "Kodagu", "Kolar", "Koppal",
        "Mandya", "Mysore", "Raichur", "Ramanagara", "Shimoga",
        "Tumkur", "Udupi", "Uttara Kannada", "Vijayapura", "Yadgir",
        "Hubli", "Belgaum", "Mangalore"
    ).sorted()

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _binding = FragmentOnboardingPage3Binding.inflate(inflater, c, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, districts)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerDistrict.adapter = adapter

        binding.btnGetStarted.setOnClickListener {
            val selected = districts[binding.spinnerDistrict.selectedItemPosition]
            (activity as OnboardingActivity).finishOnboarding(selected)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
