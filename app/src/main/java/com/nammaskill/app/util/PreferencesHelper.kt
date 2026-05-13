package com.nammaskill.app.util

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.messaging.FirebaseMessaging
import com.nammaskill.app.service.NammaSkillMessagingService

object PreferencesHelper {
    private const val PREFS_NAME = "nammaskill_prefs"
    private const val KEY_ONBOARDING_DONE = "onboarding_done"
    private const val KEY_USER_DISTRICT = "user_district"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_PHONE = "user_phone"
    private const val KEY_USER_VILLAGE = "user_village"
    private const val KEY_USER_EDUCATION = "user_education"
    private const val KEY_PREFERRED_TRADES = "preferred_trades"
    private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isOnboardingDone(context: Context) = prefs(context).getBoolean(KEY_ONBOARDING_DONE, false)
    fun setOnboardingDone(context: Context, value: Boolean) =
        prefs(context).edit().putBoolean(KEY_ONBOARDING_DONE, value).apply()

    fun getUserDistrict(context: Context) = prefs(context).getString(KEY_USER_DISTRICT, "") ?: ""
    fun setUserDistrict(context: Context, district: String) =
        prefs(context).edit().putString(KEY_USER_DISTRICT, district).apply()

    fun getUserName(context: Context) = prefs(context).getString(KEY_USER_NAME, "") ?: ""
    fun setUserName(context: Context, name: String) =
        prefs(context).edit().putString(KEY_USER_NAME, name).apply()

    fun getUserPhone(context: Context) = prefs(context).getString(KEY_USER_PHONE, "") ?: ""
    fun setUserPhone(context: Context, phone: String) =
        prefs(context).edit().putString(KEY_USER_PHONE, phone).apply()

    fun getUserVillage(context: Context) = prefs(context).getString(KEY_USER_VILLAGE, "") ?: ""
    fun setUserVillage(context: Context, village: String) =
        prefs(context).edit().putString(KEY_USER_VILLAGE, village).apply()

    fun getUserEducation(context: Context) = prefs(context).getString(KEY_USER_EDUCATION, "") ?: ""
    fun setUserEducation(context: Context, education: String) =
        prefs(context).edit().putString(KEY_USER_EDUCATION, education).apply()

    fun getPreferredTrades(context: Context): Set<String> =
        prefs(context).getStringSet(KEY_PREFERRED_TRADES, emptySet()) ?: emptySet()
    fun setPreferredTrades(context: Context, trades: Set<String>) =
        prefs(context).edit().putStringSet(KEY_PREFERRED_TRADES, trades).apply()

    fun areNotificationsEnabled(context: Context) =
        prefs(context).getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
    fun setNotificationsEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()
        // Subscribe/unsubscribe from topics
        val trades = getPreferredTrades(context)
        trades.forEach { trade ->
            val topic = NammaSkillMessagingService.TRADE_TOPICS[trade] ?: return@forEach
            if (enabled) FirebaseMessaging.getInstance().subscribeToTopic(topic)
            else FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
        }
    }

    fun subscribeToTrade(trade: String) {
        val topic = NammaSkillMessagingService.TRADE_TOPICS[trade] ?: return
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
    }

    fun unsubscribeFromTrade(trade: String) {
        val topic = NammaSkillMessagingService.TRADE_TOPICS[trade] ?: return
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
    }
}
