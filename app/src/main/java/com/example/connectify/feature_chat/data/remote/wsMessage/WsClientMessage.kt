package com.example.connectify.feature_chat.data.remote.wsMessage

data class WsClientMessage(
    val toId: String,
    val text: String,
    val chatId: String?,
)