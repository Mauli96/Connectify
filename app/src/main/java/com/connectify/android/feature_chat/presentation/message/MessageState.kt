package com.connectify.android.feature_chat.presentation.message

data class MessageState(
    val canSendMessage: Boolean = false,
    val selectedMessage: String? = null,
    val selectedMessageId: String? = null,
    val isBottomSheetVisible: Boolean = false
)