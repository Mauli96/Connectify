package com.example.connectify.feature_chat.data.repository

import android.content.SharedPreferences
import com.example.connectify.core.util.Constants
import com.example.connectify.feature_chat.domain.respository.RealtimeMessagingClient
import com.example.connectify.feature_chat.data.remote.wsMessage.WsClientMessage
import com.example.connectify.feature_chat.data.remote.wsMessage.WsServerMessage
import com.example.connectify.feature_chat.data.util.WebSocketObject
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.url
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class KtorRealtimeMessagingClient(
    private val client: HttpClient,
    private val sharedPreferences: SharedPreferences
) : RealtimeMessagingClient {

    private var session: WebSocketSession? = null

    override fun observeMessages(): Flow<WsServerMessage> {
        val token = sharedPreferences.getString(Constants.KEY_JWT_TOKEN, "")
        if (token.isNullOrEmpty()) {
            println("Token is missing, cannot connect to WebSocket")
            return flow { /* Optionally emit an error or take alternative action */ }
        }
        return flow {
            session = client.webSocketSession {
                url("ws://192.168.0.102:8001/api/chat/websocket")
                headers.append("Authorization", "Bearer $token")
            }
            val wsServerMessage = session!!
                .incoming
                .consumeAsFlow()
                .filterIsInstance<Frame.Text>()
                .mapNotNull { frame ->
                    val frameText = frame.readText()
                    fromMessage(frameText)?.let { (type, json) ->
                        if(type == WebSocketObject.MESSAGE.ordinal) {
                            Json.decodeFromString<WsServerMessage>(json)
                        } else {
                            println("Unknown message type: $type")
                            null
                        }
                    }
                }

            emitAll(wsServerMessage)
        }
    }

    override suspend fun sendMessage(message: WsClientMessage) {
        try {
            val json = Json.encodeToString(message)
            val frameText = "${WebSocketObject.MESSAGE.ordinal}#$json"
            session?.outgoing?.send(Frame.Text(frameText))
        } catch (e: Exception) {
            println("Error sending message: ${e.message}")
        }
    }

    override suspend fun close() {
        session?.close()
        session = null
    }

    private fun fromMessage(frameText: String): Pair<Int, String>? {
        val delimiterIndex = frameText.indexOf("#")
        if (delimiterIndex == -1) return null

        val type = frameText.substring(0, delimiterIndex).toIntOrNull() ?: return null
        val json = frameText.substring(delimiterIndex + 1)
        return type to json
    }
}