package com.example.connectify.di

import android.content.Context
import com.example.connectify.feature_post.data.data_source.remote.PostApi
import com.example.connectify.feature_post.data.data_source.repository.PostRepositoryImpl
import com.example.connectify.feature_post.domain.repository.PostRepository
import com.example.connectify.feature_post.domain.use_case.CreatePostUseCase
import com.example.connectify.feature_post.domain.use_case.GetPostsForFollowsUseCase
import com.example.connectify.feature_post.domain.use_case.PostUseCases
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PostModule {

    @Provides
    @Singleton
    fun providePostApi(client: OkHttpClient): PostApi {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(PostApi.BASE_URL)
            .client(client)
            .build()
            .create(PostApi::class.java)
    }

    @Provides
    @Singleton
    fun providePostRepository(
        api: PostApi,
        gson: Gson,
    ): PostRepository {
        return PostRepositoryImpl(api, gson)
    }

    @Provides
    @Singleton
    fun providePostUseCase(repository: PostRepository): PostUseCases {
        return PostUseCases(
            getPostsForFollows = GetPostsForFollowsUseCase(repository),
            createPostUseCase = CreatePostUseCase(repository)
        )
    }
}