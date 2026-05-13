package com.nammaskill.app.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.nammaskill.app.databinding.ActivitySplashBinding
import com.nammaskill.app.ui.main.MainActivity
import com.nammaskill.app.ui.onboarding.OnboardingActivity
import com.nammaskill.app.util.PreferencesHelper

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // Animate logo
        binding.logoImage.alpha = 0f
        binding.appName.alpha = 0f
        binding.tagline.alpha = 0f

        binding.logoImage.animate().alpha(1f).setDuration(600).setStartDelay(200).start()
        binding.appName.animate().alpha(1f).translationYBy(-20f).setDuration(600).setStartDelay(400).start()
        binding.tagline.animate().alpha(1f).translationYBy(-10f).setDuration(600).setStartDelay(700).start()

        Handler(Looper.getMainLooper()).postDelayed({
            val isOnboardingDone = PreferencesHelper.isOnboardingDone(this)
            val nextIntent = if (isOnboardingDone) {
                Intent(this, MainActivity::class.java)
            } else {
                Intent(this, OnboardingActivity::class.java)
            }
            startActivity(nextIntent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 2500)
    }
}
