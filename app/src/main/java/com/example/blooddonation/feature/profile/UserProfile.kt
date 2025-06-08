package com.example.blooddonation.feature.profile


data class UserProfile(
    val username:          String = "",
    val bio:               String = "",
    val bloodGroup:        String = "",
    val profileImagePath:  String = ""          // local file path
)


