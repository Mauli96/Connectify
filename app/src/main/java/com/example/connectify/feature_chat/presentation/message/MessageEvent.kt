package com.example.connectify.feature_chat.presentation.message

sealed class MessageEvent {
    object SendMessage: MessageEvent()
    data class EnteredMessage(val message: String): MessageEvent()
    data class SelectMessage(val messageId: String, val message: String): MessageEvent()
    object DeleteMessage: MessageEvent()
    object ShowBottomSheet: MessageEvent()
    object DismissBottomSheet: MessageEvent()
}