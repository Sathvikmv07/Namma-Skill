package com.nammaskill.app.ui.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.nammaskill.app.R
import com.nammaskill.app.databinding.ActivityMainBinding
import com.nammaskill.app.util.NetworkConnectivityObserver

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        // Network connectivity banner
        val networkObserver = NetworkConnectivityObserver(this)
        networkObserver.observe(this) { isConnected ->
            binding.networkBanner.visibility = if (isConnected) View.GONE else View.VISIBLE
        }

        // Handle deep link / notification intent
        intent?.let { handleDeepLink(it) }
    }

    private fun handleDeepLink(intent: android.content.Intent) {
        val courseId = intent.getStringExtra("courseId")
        val action = intent.getStringExtra("action")
        if (courseId != null) {
            // Navigate to course detail
            val bundle = Bundle().apply { putString("courseId", courseId) }
            navController.navigate(R.id.courseDetailFragment, bundle)
        }
        val tab = intent.getStringExtra("tab")
        if (tab == "profile") {
            binding.bottomNavigationView.selectedItemId = R.id.navigation_profile
        }
    }
}
