package com.connectify.android.di

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.connectify.android.R
import com.connectify.android.core.presentation.MainActivity
import com.connectify.android.core.util.DefaultPostDownloader
import com.connectify.android.core.util.PostDownloader
import com.connectify.android.feature_post.data.remote.PostApi
import com.connectify.android.feature_post.data.repository.PostRepositoryImpl
import com.connectify.android.feature_post.domain.repository.PostRepository
import com.connectify.android.feature_post.domain.use_case.CreateCommentUseCase
import com.connectify.android.feature_post.domain.use_case.CreatePostUseCase
import com.connectify.android.feature_post.domain.use_case.DeleteCommentUseCase
import com.connectify.android.feature_post.domain.use_case.DeletePostUseCase
import com.connectify.android.feature_post.domain.use_case.GetCommentsForPostUseCase
import com.connectify.android.feature_post.domain.use_case.GetLikesForParentUseCase
import com.connectify.android.feature_post.domain.use_case.GetPostDetailsUseCase
import com.connectify.android.feature_post.domain.use_case.GetPostsForFollowsUseCase
import com.connectify.android.feature_post.domain.use_case.GetSavedPostsUseCase
import com.connectify.android.feature_post.domain.use_case.PostUseCases
import com.connectify.android.feature_post.domain.use_case.ToggleSavePostUseCase
import com.connectify.android.feature_post.domain.use_case.ToggleLikeForParentUseCase
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
            toggleLikeForParent = ToggleLikeForParentUseCase(repository),
            getLikesForParent = GetLikesForParentUseCase(repository),
            deletePost = DeletePostUseCase(repository),
            deleteComment = DeleteCommentUseCase(repository),
            getSavedPosts = GetSavedPostsUseCase(repository),
            toggleSavePost = ToggleSavePostUseCase(repository)
        )
    }

    @Singleton
    @Provides
    fun provideNotificationBuilder(
        @ApplicationContext context: Context
    ): NotificationCompat.Builder {
        val flag = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            0
        }
        val clickIntent = Intent(
            Intent.ACTION_VIEW,
            "https://connectify.com/profile".toUri(),
            context,
            MainActivity::class.java
        )
        val clickPendingIntent: PendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(clickIntent)
            getPendingIntent(1, flag)
        }

        return NotificationCompat.Builder(context, "post_notification_channel")
            .setContentTitle("Post Created")
            .setContentText("Your post was successfully shared with your followers!")
            .setSmallIcon(R.drawable.ic_logo)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(clickPendingIntent)
            .setAutoCancel(true)
    }

    @Singleton
    @Provides
    fun provideNotificationManager(
        @ApplicationContext context: Context
    ): NotificationManagerCompat {
        val notificationManager = NotificationManagerCompat.from(context)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "post_notification_channel",
                "Post Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            context.getSystemService(NotificationManager::class.java)
                ?.createNotificationChannel(channel)
        }
        return notificationManager
    }

    @Singleton
    @Provides
    fun providePostDownloader(
        @ApplicationContext context: Context
    ): PostDownloader {
        return DefaultPostDownloader(context)
    }
}