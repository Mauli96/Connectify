package com.example.connectify.feature_chat.presentation.message

data class MessageState(
    val canSendMessage: Boolean = false,
    val deleteMessageId: String = "",
    val isDialogVisible: Boolean = false
)