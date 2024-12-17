package com.example.connectify.feature_chat.data.remote.wsMessage

import com.example.connectify.feature_activity.presentation.util.DateFormatUtil
import com.example.connectify.feature_chat.domain.model.Message
import kotlinx.serialization.Serializable

@Serializable
data class WsServerMessage(
    val fromId: String,
    val toId: String,
    val text: String,
    val timestamp: Long,
    val chatId: String?,
    val id: String
) {
    fun toMessage(): Message {
        return Message(
            fromId = fromId,
            toId = toId,
            text = text,
            formattedTime = DateFormatUtil.timestampToFormattedString(timestamp, "hh:mm a"),
            chatId = chatId,
            id = id
        )
    }
}