package com.example.connectify.feature_chat.presentation.message

import com.example.connectify.feature_chat.domain.model.Message

data class MessageState(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val canSendMessage: Boolean = false
)