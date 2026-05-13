package com.nammaskill.app.data.model

import com.google.firebase.firestore.DocumentId

data class Application(
    @DocumentId val id: String = "",
    val courseId: String = "",
    val courseName: String = "",
    val candidateName: String = "",
    val age: Int = 0,
    val phone: String = "",
    val village: String = "",
    val district: String = "",
    val education: String = "",
    val experience: String = "",
    val language: String = "Kannada",
    val appliedDate: String = "",
    val status: String = "Submitted" // Submitted / Under Review / Approved / Batch Started
) {
    fun toMap(): Map<String, Any> = mapOf(
        "courseId" to courseId,
        "courseName" to courseName,
        "candidateName" to candidateName,
        "age" to age,
        "phone" to phone,
        "village" to village,
        "district" to district,
        "education" to education,
        "experience" to experience,
        "language" to language,
        "appliedDate" to appliedDate,
        "status" to status
    )
    fun toSummaryString(): String = """
--- CANDIDATE SUMMARY ---
Name       : $candidateName
Age        : $age years
Contact    : +91 $phone
Location   : $village, $district
Education  : $education
Applying For: $courseName
Experience : ${experience.ifEmpty { "Fresher" }}
Language   : $language
Date       : $appliedDate
--------------------------""".trimIndent()
}
