package com.example.blooddonation.feature.chat

/**
 * Always returns the SAME id for the same pair of users,
 * no matter who calls it first.
 *
 * Examples:
 *   generateChatId("A", "B")  // ->  "A_B"
 *   generateChatId("B", "A")  // ->  "A_B"   (same!)
 */
fun generateChatId(user1: String, user2: String): String {
    return if (user1 < user2) "${user1}_$user2" else "${user2}_$user1"
}

