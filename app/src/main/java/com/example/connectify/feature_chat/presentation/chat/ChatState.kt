package com.example.connectify.feature_chat.presentation.chat

import com.example.connectify.feature_chat.domain.model.Chat

data class ChatState(
    val chats: List<Chat> = emptyList(),
    val selectedChat: String? = null,
    val selectedChatName: String? = null,
    val isBottomSheetVisible: Boolean = false,
    val isLoading: Boolean = false
)