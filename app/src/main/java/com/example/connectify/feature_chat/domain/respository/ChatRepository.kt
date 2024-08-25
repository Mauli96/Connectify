package com.example.connectify.feature_chat.domain.respository

import com.example.connectify.core.util.Resource
import com.example.connectify.core.util.SimpleResource
import com.example.connectify.feature_chat.domain.model.Chat
import com.example.connectify.feature_chat.domain.model.Message
import com.tinder.scarlet.WebSocket
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    fun initialize()

    suspend fun getChatsForUser(): Resource<List<Chat>>

    suspend fun getMessagesForChat(
        chatId: String,
        page: Int,
        pageSize: Int
    ): Resource<List<Message>>

    fun observeChatEvents(): Flow<WebSocket.Event>

    fun observeMessages(): Flow<Message>

    fun sendMessage(
        toId: String,
        text: String,
        chatId: String?
    )

    suspend fun deleteChat(chatId: String): SimpleResource

    suspend fun deleteMessage(messageId: String): SimpleResource
}