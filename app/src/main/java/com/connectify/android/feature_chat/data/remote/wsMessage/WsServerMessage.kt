package com.connectify.android.feature_chat.data.remote.wsMessage

import com.connectify.android.feature_activity.presentation.util.DateFormatUtil
import com.connectify.android.feature_chat.domain.model.Message
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