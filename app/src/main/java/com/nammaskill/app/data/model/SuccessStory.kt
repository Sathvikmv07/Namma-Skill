package com.nammaskill.app.data.model

import com.google.firebase.firestore.DocumentId

data class SuccessStory(
    @DocumentId val id: String = "",
    val name: String = "",
    val age: Int = 0,
    val village: String = "",
    val district: String = "",
    val trade: String = "",
    val beforeStory: String = "",
    val afterStory: String = "",
    val currentSalary: Int = 0,
    val centerName: String = "",
    val quote: String = ""
) {
    fun toMap(): Map<String, Any> = mapOf(
        "name" to name,
        "age" to age,
        "village" to village,
        "district" to district,
        "trade" to trade,
        "beforeStory" to beforeStory,
        "afterStory" to afterStory,
        "currentSalary" to currentSalary,
        "centerName" to centerName,
        "quote" to quote
    )
}
