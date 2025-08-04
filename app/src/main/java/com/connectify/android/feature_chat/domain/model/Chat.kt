package com.connectify.android.feature_chat.domain.model

data class Chat(
    val chatId: String,
    val remoteUserId: String,
    val remoteUsername: String,
    val remoteUserProfilePictureUrl: String,
    val online: Boolean?,
    val lastSeen: Long?,
    val lastMessage: String,
    val timestamp: Long
)