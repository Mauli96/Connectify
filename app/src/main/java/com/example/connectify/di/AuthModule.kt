package com.example.connectify.di

import android.content.Context
import android.content.SharedPreferences
import com.example.connectify.feature_auth.data.remote.AuthApi
import com.example.connectify.feature_auth.data.repository.AuthRepositoryImpl
import com.example.connectify.feature_auth.data.repository.DataStoreRepositoryImpl
import com.example.connectify.feature_auth.domain.repository.AuthRepository
import com.example.connectify.feature_auth.domain.repository.DataStoreRepository
import com.example.connectify.feature_auth.domain.use_case.AuthenticateUseCase
import com.example.connectify.feature_auth.domain.use_case.ForgotPasswordUseCase
import com.example.connectify.feature_auth.domain.use_case.GenerateOtpUseCase
import com.example.connectify.feature_auth.domain.use_case.LoginUseCase
import com.example.connectify.feature_auth.domain.use_case.ReadOnBoardingStateUseCase
import com.example.connectify.feature_auth.domain.use_case.RegisterUseCase
import com.example.connectify.feature_auth.domain.use_case.SaveOnBoardingStateUseCase
import com.example.connectify.feature_auth.domain.use_case.VerifyOtpUseCase
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
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthApi(client: OkHttpClient): AuthApi {
        return Retrofit.Builder()
            .baseUrl(AuthApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        api: AuthApi,
        sharedPreferences: SharedPreferences
    ): AuthRepository {
        return AuthRepositoryImpl(api, sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideDataStoreRepository(
        @ApplicationContext context: Context
    ): DataStoreRepository {
        return DataStoreRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideRegisterUseCase(repository: AuthRepository): RegisterUseCase {
        return RegisterUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideLoginUseCase(repository: AuthRepository): LoginUseCase {
        return LoginUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSaveOnBoardingStateUseCase(repository: DataStoreRepository): SaveOnBoardingStateUseCase {
        return SaveOnBoardingStateUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideReadOnBoardingStateUseCase(repository: DataStoreRepository): ReadOnBoardingStateUseCase {
        return ReadOnBoardingStateUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGenerateOtpUseCase(repository: AuthRepository): GenerateOtpUseCase {
        return GenerateOtpUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideVerifyOtpUseCase(repository: AuthRepository): VerifyOtpUseCase {
        return VerifyOtpUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideForgotPasswordUseCase(repository: AuthRepository): ForgotPasswordUseCase {
        return ForgotPasswordUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideAuthenticationUseCase(repository: AuthRepository): AuthenticateUseCase {
        return AuthenticateUseCase(repository)
    }
}