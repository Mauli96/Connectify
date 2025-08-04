package com.connectify.android.feature_chat.domain.respository

import com.connectify.android.core.util.Resource
import com.connectify.android.core.util.SimpleResource
import com.connectify.android.feature_chat.domain.model.Chat
import com.connectify.android.feature_chat.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    suspend fun getChatsForUser(): Resource<List<Chat>>

    suspend fun getMessagesForChat(
        chatId: String,
        page: Int,
        pageSize: Int
    ): Resource<List<Message>>


    fun observeMessages(): Flow<Message>

    suspend fun sendMessage(
        toId: String,
        text: String,
        chatId: String?
    )

    suspend fun close()

    suspend fun deleteChat(chatId: String): SimpleResource

    suspend fun deleteMessage(messageId: String): SimpleResource
}