package com.connectify.android.feature_chat.data.repository

import com.connectify.android.R
import com.connectify.android.core.util.Resource
import com.connectify.android.core.util.SimpleResource
import com.connectify.android.core.util.UiText
import com.connectify.android.feature_chat.data.remote.ChatApi
import com.connectify.android.feature_chat.data.remote.wsMessage.WsClientMessage
import com.connectify.android.feature_chat.domain.model.Chat
import com.connectify.android.feature_chat.domain.model.Message
import com.connectify.android.feature_chat.domain.respository.ChatRepository
import com.connectify.android.feature_chat.domain.respository.RealtimeMessagingClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException

class ChatRepositoryImpl(
    private val chatApi: ChatApi,
    private val client: RealtimeMessagingClient
): ChatRepository {

    override suspend fun getChatsForUser(): Resource<List<Chat>> {
        return try {
            val chats = chatApi
                .getChatsForUser()
                .mapNotNull { it.toChat() }
            Resource.Success(data = chats)
        } catch(e: IOException) {
            Resource.Error(
                uiText = UiText.StringResource(R.string.error_couldnt_reach_server)
            )
        } catch(e: HttpException) {
            Resource.Error(
                uiText = UiText.StringResource(R.string.oops_something_went_wrong)
            )
        }
    }

    override suspend fun getMessagesForChat(
        chatId: String,
        page: Int,
        pageSize: Int
    ): Resource<List<Message>> {
        return try {
            val messages = chatApi
                .getMessagesForChat(chatId, page, pageSize)
                .map { it.toMessage() }
            Resource.Success(data = messages)
        } catch(e: IOException) {
            Resource.Error(
                uiText = UiText.StringResource(R.string.error_couldnt_reach_server)
            )
        } catch(e: HttpException) {
            Resource.Error(
                uiText = UiText.StringResource(R.string.oops_something_went_wrong)
            )
        }
    }

    override fun observeMessages(): Flow<Message> {
        return client.observeMessages().map {
            it.toMessage()
        }
    }

    override suspend fun sendMessage(
        toId: String,
        text: String,
        chatId: String?
    ) {
        client.sendMessage(
            WsClientMessage(
                toId = toId,
                text = text,
                chatId = chatId
            )
        )
    }

    override suspend fun close() {
        client.close()
    }

    override suspend fun deleteChat(chatId: String): SimpleResource {
        return try {
            chatApi.deleteChat(chatId)
            Resource.Success(Unit)
        } catch(e: IOException) {
            Resource.Error(
                uiText = UiText.StringResource(R.string.error_couldnt_reach_server)
            )
        } catch(e: HttpException) {
            Resource.Error(
                uiText = UiText.StringResource(R.string.oops_something_went_wrong)
            )
        }
    }

    override suspend fun deleteMessage(messageId: String): SimpleResource {
        return try {
            chatApi.deleteMessage(messageId)
            Resource.Success(Unit)
        } catch(e: IOException) {
            Resource.Error(
                uiText = UiText.StringResource(R.string.error_couldnt_reach_server)
            )
        } catch(e: HttpException) {
            Resource.Error(
                uiText = UiText.StringResource(R.string.oops_something_went_wrong)
            )
        }
    }
}