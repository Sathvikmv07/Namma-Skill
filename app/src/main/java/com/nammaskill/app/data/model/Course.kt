package com.nammaskill.app.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Course(
    @DocumentId val id: String = "",
    val title: String = "",
    val trade: String = "",
    val centerName: String = "",
    val district: String = "",
    val duration: Int = 0,
    val durationType: String = "short", // short / long / advanced
    val startDate: String = "",
    val seatsAvailable: Int = 0,
    val totalSeats: Int = 0,
    val eligibility: String = "",
    val jobGuaranteed: Boolean = false,
    val stipend: Int = 0,
    val description: String = "",
    val trainerName: String = "",
    val trainerContact: String = "",
    val tags: List<String> = emptyList()
) {
    val occupancyPercent: Int get() = if (totalSeats > 0) ((totalSeats - seatsAvailable) * 100) / totalSeats else 0
    val durationLabel: String get() = when (durationType) {
        "short" -> "Short Term (<3 months)"
        "long" -> "Long Term (3–6 months)"
        "advanced" -> "Advanced (6+ months)"
        else -> "$duration months"
    }
    fun toMap(): Map<String, Any> = mapOf(
        "title" to title,
        "trade" to trade,
        "centerName" to centerName,
        "district" to district,
        "duration" to duration,
        "durationType" to durationType,
        "startDate" to startDate,
        "seatsAvailable" to seatsAvailable,
        "totalSeats" to totalSeats,
        "eligibility" to eligibility,
        "jobGuaranteed" to jobGuaranteed,
        "stipend" to stipend,
        "description" to description,
        "trainerName" to trainerName,
        "trainerContact" to trainerContact,
        "tags" to tags
    )
}
