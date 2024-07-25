package com.example.connectify.di

import com.example.connectify.feature_post.data.remote.PostApi
import com.example.connectify.feature_post.data.repository.PostRepositoryImpl
import com.example.connectify.feature_post.domain.repository.PostRepository
import com.example.connectify.feature_post.domain.use_case.CreateCommentUseCase
import com.example.connectify.feature_post.domain.use_case.CreatePostUseCase
import com.example.connectify.feature_post.domain.use_case.GetCommentsForPostUseCase
import com.example.connectify.feature_post.domain.use_case.GetPostDetailsUseCase
import com.example.connectify.feature_post.domain.use_case.GetPostsForFollowsUseCase
import com.example.connectify.feature_post.domain.use_case.PostUseCases
import com.example.connectify.feature_post.domain.use_case.ToggleLikeForParentUseCase
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
            .baseUrl(PostApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
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
            createPost = CreatePostUseCase(repository),
            getPostDetails = GetPostDetailsUseCase(repository),
            getCommentsForPost = GetCommentsForPostUseCase(repository),
            createComment = CreateCommentUseCase(repository),
            toggleLikeForParent = ToggleLikeForParentUseCase(repository)
        )
    }
}