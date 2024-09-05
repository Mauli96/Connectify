package com.example.connectify.feature_chat.presentation.chat

sealed class ChatEvent {
    data class SelectChat(val chatId: String): ChatEvent()
    object DeleteChat: ChatEvent()
    object ShowBottomSheet: ChatEvent()
    object DismissBottomSheet: ChatEvent()
}