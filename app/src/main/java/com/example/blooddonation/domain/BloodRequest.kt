package com.example.blooddonation.domain



data class BloodRequest(
    val id: String = "",
    val requesterId: String = "",
    val requesterName: String? = null,
    val timestamp: Long = 0L,
    val bloodGroup: String = "",
    val location: String = "",
    val status: String = "pending",
    val acceptedBy: String? = null,
    val chatId: String? = null
)

data class Acceptance(
    val id: String = "",
    val requestId: String = "",
    val donorId: String = "",
    val medicalInfo: String = ""
)


