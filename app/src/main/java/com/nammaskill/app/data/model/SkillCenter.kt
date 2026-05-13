package com.nammaskill.app.data.model

import com.google.firebase.firestore.DocumentId

data class SkillCenter(
    @DocumentId val id: String = "",
    val name: String = "",
    val address: String = "",
    val district: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val phone: String = "",
    val tradesOffered: List<String> = emptyList()
) {
    fun toMap(): Map<String, Any> = mapOf(
        "name" to name,
        "address" to address,
        "district" to district,
        "lat" to lat,
        "lng" to lng,
        "phone" to phone,
        "tradesOffered" to tradesOffered
    )
}
