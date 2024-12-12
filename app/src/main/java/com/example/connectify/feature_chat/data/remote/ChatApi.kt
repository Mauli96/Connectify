package com.example.connectify.feature_chat.data.remote

import com.example.connectify.feature_chat.data.remote.response.ChatDto
import com.example.connectify.feature_chat.data.remote.response.MessageDto
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Query

interface ChatApi {

    @GET("/api/chats")
    suspend fun getChatsForUser(): List<ChatDto>

    @GET("/api/chat/messages")
    suspend fun getMessagesForChat(
        @Query("chatId") chatId: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): List<MessageDto>

    @DELETE("/api/chat/delete")
    suspend fun deleteChat(
        @Query("chatId") chatId: String
    )

    @DELETE("/api/chat/message/delete")
    suspend fun deleteMessage(
        @Query("messageId") messageId: String
    )

    companion object {
        const val BASE_URL = "http://192.168.0.101:8001/"
    }
}