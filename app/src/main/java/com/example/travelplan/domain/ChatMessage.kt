package com.example.travelplan.domain

data class ChatMessage(
    val id: String = "",
    val senderEmail: String = "",
    val messageText: String = "",
    val timestamp: Long = 0L
)