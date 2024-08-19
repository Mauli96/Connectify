package com.example.connectify.feature_chat.presentation.chat

sealed class ChatEvent {
    data class DeleteChatId(val chatId: String): ChatEvent()
    data class DeleteChat(val chatId: String): ChatEvent()
    object ShowBottomSheet: ChatEvent()
    object DismissBottomSheet: ChatEvent()
}