package com.example.connectify.feature_chat.presentation.chat

import com.example.connectify.feature_chat.domain.model.Chat

data class ChatState(
    val chats: List<Chat> = emptyList(),
    val isBottomSheetVisible: Boolean = false,
    val deleteChatId: String = "",
    val isLoading: Boolean = false
)