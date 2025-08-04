package com.connectify.android.di

import android.content.SharedPreferences
import com.connectify.android.feature_post.data.remote.PostApi
import com.connectify.android.feature_profile.data.remote.ProfileApi
import com.connectify.android.core.data.repository.ProfileRepositoryImpl
import com.connectify.android.core.domain.repository.ProfileRepository
import com.connectify.android.feature_profile.domain.use_case.GetPostsForProfileUseCase
import com.connectify.android.feature_profile.domain.use_case.GetProfileUseCase
import com.connectify.android.feature_profile.domain.use_case.GetSkillsUseCase
import com.connectify.android.feature_profile.domain.use_case.ProfileUseCases
import com.connectify.android.feature_profile.domain.use_case.SearchUserUseCase
import com.connectify.android.feature_profile.domain.use_case.SetSkillSelectedUseCase
import com.connectify.android.core.domain.use_case.ToggleFollowStateForUserUseCase
import com.connectify.android.feature_profile.domain.use_case.GetFollowedToUserUseCase
import com.connectify.android.feature_profile.domain.use_case.GetFollowsByUserUseCase
import com.connectify.android.feature_profile.domain.use_case.LogoutUseCase
import com.connectify.android.feature_profile.domain.use_case.UpdateProfileUseCase
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
object ProfileModule {

    @Provides
    @Singleton
    fun provideProfileApi(client: OkHttpClient): ProfileApi {
        return Retrofit.Builder()
            .baseUrl(ProfileApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ProfileApi::class.java)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(profileApi: ProfileApi, postApi: PostApi, gson: Gson, sharedPreferences: SharedPreferences): ProfileRepository {
        return ProfileRepositoryImpl(profileApi, postApi, gson, sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideProfileUseCase(repository: ProfileRepository): ProfileUseCases {
        return ProfileUseCases(
            getProfile = GetProfileUseCase(repository),
            getSkills = GetSkillsUseCase(repository),
            updateProfile = UpdateProfileUseCase(repository),
            setSkillUseCase = SetSkillSelectedUseCase(),
            getPostsForProfile = GetPostsForProfileUseCase(repository),
            searchUser = SearchUserUseCase(repository),
            toggleFollowStateForUser = ToggleFollowStateForUserUseCase(repository),
            getFollowsByUser = GetFollowsByUserUseCase(repository),
            getFollowedToUser = GetFollowedToUserUseCase(repository),
            logout = LogoutUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun provideToggleFollowForUserUseCase(repository: ProfileRepository): ToggleFollowStateForUserUseCase {
        return ToggleFollowStateForUserUseCase(repository)
    }
}