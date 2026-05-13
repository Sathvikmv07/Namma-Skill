package com.nammaskill.app.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.nammaskill.app.R
import com.nammaskill.app.ui.main.MainActivity

object NotificationHelper {

    const val CHANNEL_COURSES = "nammaskill_courses"
    const val CHANNEL_APPLICATIONS = "nammaskill_applications"
    const val CHANNEL_GENERAL = "nammaskill_general"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Courses channel
            val coursesChannel = NotificationChannel(
                CHANNEL_COURSES,
                "New Courses & Batches",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications about new skill courses and batch openings"
                enableLights(true)
                enableVibration(true)
            }

            // Applications channel
            val applicationsChannel = NotificationChannel(
                CHANNEL_APPLICATIONS,
                "Application Updates",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Updates on your course applications"
            }

            // General channel
            val generalChannel = NotificationChannel(
                CHANNEL_GENERAL,
                "General Updates",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "General NammaSkill announcements"
            }

            manager.createNotificationChannels(listOf(coursesChannel, applicationsChannel, generalChannel))
        }
    }

    fun showCourseNotification(context: Context, title: String, body: String, courseId: String?) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            courseId?.let { putExtra("courseId", it) }
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action: View Course
        val viewIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("action", "view_course")
            courseId?.let { putExtra("courseId", it) }
        }
        val viewPending = PendingIntent.getActivity(
            context, 1, viewIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action: Apply Now
        val applyIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("action", "apply_now")
            courseId?.let { putExtra("courseId", it) }
        }
        val applyPending = PendingIntent.getActivity(
            context, 2, applyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_COURSES)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(R.drawable.ic_eye, "View Course", viewPending)
            .addAction(R.drawable.ic_apply, "Apply Now", applyPending)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(System.currentTimeMillis().toInt(), notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun showApplicationUpdateNotification(context: Context, title: String, body: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("tab", "profile")
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_APPLICATIONS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        try {
            NotificationManagerCompat.from(context).notify(System.currentTimeMillis().toInt(), notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}
