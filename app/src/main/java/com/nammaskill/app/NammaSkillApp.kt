package com.nammaskill.app

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.nammaskill.app.data.repository.FirebaseRepository
import com.nammaskill.app.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class NammaSkillApp : Application() {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        try {
            FirebaseApp.initializeApp(this)
            NotificationHelper.createNotificationChannels(this)
            
            // Seed Firestore with sample data on first launch
            applicationScope.launch(Dispatchers.IO) {
                try {
                    FirebaseRepository.seedInitialData()
                } catch (e: Exception) {
                    Log.e("NammaSkillApp", "Seed error: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("NammaSkillApp", "Init error: ${e.message}")
        }
    }
}
