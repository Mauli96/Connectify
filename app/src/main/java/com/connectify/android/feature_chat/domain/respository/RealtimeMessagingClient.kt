package com.connectify.android.feature_chat.domain.respository

import com.connectify.android.feature_chat.data.remote.wsMessage.WsClientMessage
import com.connectify.android.feature_chat.data.remote.wsMessage.WsServerMessage
import kotlinx.coroutines.flow.Flow

interface RealtimeMessagingClient {
    fun observeMessages(): Flow<WsServerMessage>
    suspend fun sendMessage(message: WsClientMessage)
    suspend fun close()
}