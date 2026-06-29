package com.gideon.shambaiq

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: String
)