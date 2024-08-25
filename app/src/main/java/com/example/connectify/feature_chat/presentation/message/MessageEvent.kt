package com.example.connectify.feature_chat.presentation.message

sealed class MessageEvent {
    object SendMessage: MessageEvent()
    data class EnteredMessage(val message: String): MessageEvent()
    data class DeleteMessageId(val messageId: String): MessageEvent()
    data class DeleteMessage(val messageId: String): MessageEvent()
    object ShowDialog: MessageEvent()
    object DismissDialog: MessageEvent()
}