package com.nammaskill.app.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.nammaskill.app.util.NotificationHelper

class NammaSkillMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Token refreshed - store or re-subscribe to topics
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title ?: message.data["title"] ?: "NammaSkill"
        val body = message.notification?.body ?: message.data["body"] ?: ""
        val type = message.data["type"] ?: "general"
        val courseId = message.data["courseId"]

        when (type) {
            "new_course", "new_batch" -> {
                NotificationHelper.showCourseNotification(this, title, body, courseId)
            }
            "application_update" -> {
                NotificationHelper.showApplicationUpdateNotification(this, title, body)
            }
            else -> {
                NotificationHelper.showCourseNotification(this, title, body, null)
            }
        }
    }

    companion object {
        // FCM topic subscriptions by trade
        val TRADE_TOPICS = mapOf(
            "Electrician" to "new_batches_electrician",
            "Welding" to "new_batches_welding",
            "Sewing" to "new_batches_sewing",
            "Mobile Repair" to "new_batches_mobile_repair",
            "Coding" to "new_batches_coding",
            "Plumbing" to "new_batches_plumbing",
            "Carpentry" to "new_batches_carpentry"
        )
    }
}
