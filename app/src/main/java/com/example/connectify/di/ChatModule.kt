package com.example.connectify.di

import android.content.SharedPreferences
import com.example.connectify.feature_chat.data.remote.ChatApi
import com.example.connectify.feature_chat.data.repository.ChatRepositoryImpl
import com.example.connectify.feature_chat.data.repository.KtorRealtimeMessagingClient
import com.example.connectify.feature_chat.domain.respository.ChatRepository
import com.example.connectify.feature_chat.domain.respository.RealtimeMessagingClient
import com.example.connectify.feature_chat.domain.use_case.ChatUseCases
import com.example.connectify.feature_chat.domain.use_case.CloseWsConnection
import com.example.connectify.feature_chat.domain.use_case.DeleteChat
import com.example.connectify.feature_chat.domain.use_case.DeleteMessage
import com.example.connectify.feature_chat.domain.use_case.GetChatsForUser
import com.example.connectify.feature_chat.domain.use_case.GetMessagesForChat
import com.example.connectify.feature_chat.domain.use_case.ObserveMessages
import com.example.connectify.feature_chat.domain.use_case.SendMessage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Module
@InstallIn(SingletonComponent::class)
object ChatModule {

    @Singleton
    @Provides
    fun provideHttpClient(): HttpClient {
        return HttpClient(CIO) {
            install(Logging)
            install(WebSockets)
        }
    }

    @Provides
    @Singleton
    fun provideKtorMessagingClient(
        httpClient: HttpClient,
        sharedPreferences: SharedPreferences
    ): RealtimeMessagingClient {
        return KtorRealtimeMessagingClient(httpClient, sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideChatUseCases(repository: ChatRepository): ChatUseCases {
        return ChatUseCases(
            sendMessage = SendMessage(repository),
            observeMessages = ObserveMessages(repository),
            getChatsForUser = GetChatsForUser(repository),
            getMessagesForChat = GetMessagesForChat(repository),
            closeWsConnection = CloseWsConnection(repository),
            deleteChat = DeleteChat(repository),
            deleteMessage = DeleteMessage(repository)
        )
    }

    @Provides
    @Singleton
    fun provideChatApi(client: OkHttpClient): ChatApi {
        return Retrofit.Builder()
            .baseUrl(ChatApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create()
    }

    @Provides
    @Singleton
    fun provideChatRepository(
        chatApi: ChatApi,
        client: RealtimeMessagingClient
    ): ChatRepository {
        return ChatRepositoryImpl(chatApi, client)
    }
}