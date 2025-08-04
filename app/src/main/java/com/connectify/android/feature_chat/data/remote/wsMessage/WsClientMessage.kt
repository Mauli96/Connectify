package com.connectify.android.feature_chat.data.remote.wsMessage

import kotlinx.serialization.Serializable

@Serializable
data class WsClientMessage(
    val toId: String,
    val text: String,
    val chatId: String?
)