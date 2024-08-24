package com.example.connectify.feature_chat.presentation.message

data class MessageState(
    val canSendMessage: Boolean = false,
    val isDialogVisible: Boolean = false
)