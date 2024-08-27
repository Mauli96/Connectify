package com.example.connectify.feature_chat.data.remote.response

import com.example.connectify.feature_chat.domain.model.Chat

data class ChatDto(
    val chatId: String,
    val remoteUserId: String?,
    val remoteUsername: String?,
    val remoteUserProfilePictureUrl: String?,
    val online: Boolean?,
    val lastSeen: Long?,
    val lastMessage: String?,
    val timestamp: Long?
) {
    fun toChat(): Chat? {
        return Chat(
            chatId = chatId,
            remoteUserId = remoteUserId ?: return null,
            remoteUsername = remoteUsername ?: return null,
            remoteUserProfilePictureUrl = remoteUserProfilePictureUrl ?: return null,
            online = online,
            lastSeen = lastSeen,
            lastMessage = lastMessage ?: return null,
            timestamp = timestamp ?: return null
        )
    }
}