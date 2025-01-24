package com.example.connectify.di

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.multidex.BuildConfig
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.util.DebugLogger
import com.example.connectify.core.data.connectivity.AndroidConnectivityObserver
import com.example.connectify.core.data.connectivity.ConnectivityObserver
import com.example.connectify.core.data.repository.ImageRepositoryImpl
import com.example.connectify.core.domain.repository.ImageRepository
import com.example.connectify.core.domain.repository.ProfileRepository
import com.example.connectify.core.domain.use_case.GetOwnProfilePictureUseCase
import com.example.connectify.core.domain.use_case.GetOwnUserIdUseCase
import com.example.connectify.core.domain.use_case.GetPostDownloadUrlUseCase
import com.example.connectify.core.domain.use_case.LoadImageUseCase
import com.example.connectify.core.domain.use_case.CompressImageUseCase
import com.example.connectify.core.util.CommentLiker
import com.example.connectify.core.util.Constants
import com.example.connectify.core.util.DefaultCommentLiker
import com.example.connectify.core.util.DefaultPostLiker
import com.example.connectify.core.util.DefaultPostSaver
import com.example.connectify.core.util.PostLiker
import com.example.connectify.core.util.PostSaver
import com.example.connectify.feature_post.domain.repository.PostRepository
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSharedPref(app: Application): SharedPreferences {
        return app.getSharedPreferences(
            Constants.SHARED_PREF_NAME,
            MODE_PRIVATE
        )
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(sharedPreferences: SharedPreferences): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor {
                val token = sharedPreferences.getString(Constants.KEY_JWT_TOKEN, "")
                val modifiedRequest = it.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                it.proceed(modifiedRequest)
            }
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideImageLoader(
        context: Application
    ): ImageLoader {
        return ImageLoader.Builder(context)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.2)
                    .strongReferencesEnabled(true)
                    .build()
            }
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCache {
                DiskCache.Builder()
                    .maxSizePercent(0.1)
                    .directory(context.cacheDir.resolve("image_cache"))
                    .build()
            }
            .crossfade(true)
            .components {
                add(SvgDecoder.Factory(true))
            }
            .apply {
                if(BuildConfig.DEBUG) {
                    logger(DebugLogger())
                }
            }
            .build()
    }

    @Provides
    @Singleton
    fun providesConnectivityObserver(@ApplicationContext context: Context): ConnectivityObserver {
        return AndroidConnectivityObserver(context)
    }

    @Provides
    @Singleton
    fun providePostLiker(): PostLiker {
        return DefaultPostLiker()
    }

    @Provides
    @Singleton
    fun provideCommentLiker(): CommentLiker {
        return DefaultCommentLiker()
    }

    @Provides
    @Singleton
    fun providePostSaver(): PostSaver {
        return DefaultPostSaver()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun provideGetOwnUserIdUseCase(sharedPreferences: SharedPreferences): GetOwnUserIdUseCase {
        return GetOwnUserIdUseCase(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideGetOwnProfilePictureUseCase(profileRepository: ProfileRepository): GetOwnProfilePictureUseCase {
        return GetOwnProfilePictureUseCase(profileRepository)
    }

    @Provides
    @Singleton
    fun provideGetPostDownloadUrlUseCase(postRepository: PostRepository): GetPostDownloadUrlUseCase {
        return GetPostDownloadUrlUseCase(postRepository)
    }

    @Provides
    @Singleton
    fun provideImageRepository(
        @ApplicationContext context: Context,
        imageLoader: ImageLoader
    ): ImageRepository {
        return ImageRepositoryImpl(context, imageLoader)
    }

    @Provides
    @Singleton
    fun provideLoadImageUseCase(repository: ImageRepository): LoadImageUseCase {
        return LoadImageUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideCompressImageUseCase(repository: ImageRepository): CompressImageUseCase {
        return CompressImageUseCase(repository)
    }
}