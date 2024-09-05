package com.example.connectify.feature_chat.presentation.message

data class MessageState(
    val canSendMessage: Boolean = false,
    val selectedMessageId: String? = null,
    val isDialogVisible: Boolean = false
)