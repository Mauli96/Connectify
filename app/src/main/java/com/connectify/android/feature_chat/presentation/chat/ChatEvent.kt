package com.connectify.android.feature_chat.presentation.chat

sealed class ChatEvent {
    data class SelectChat(val chatId: String, val chatName: String): ChatEvent()
    object DeleteChat: ChatEvent()
    object ShowBottomSheet: ChatEvent()
    object DismissBottomSheet: ChatEvent()
}