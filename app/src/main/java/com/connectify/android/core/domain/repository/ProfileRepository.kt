package com.connectify.android.core.domain.repository

import android.net.Uri
import com.connectify.android.core.domain.models.Post
import com.connectify.android.core.domain.models.UserItem
import com.connectify.android.core.util.Resource
import com.connectify.android.core.util.SimpleResource
import com.connectify.android.feature_profile.domain.models.Profile
import com.connectify.android.feature_profile.domain.models.Skill
import com.connectify.android.feature_profile.domain.models.UpdateProfileData

interface ProfileRepository {

    suspend fun getPostsPaged(
        userId: String,
        page: Int,
        pageSize: Int
    ): Resource<List<Post>>

    suspend fun getProfile(userId: String): Resource<Profile>

    suspend fun updateProfile(
        bannerImageUri: Uri?,
        profilePictureUri: Uri?,
        updateProfileData: UpdateProfileData
    ): SimpleResource

    suspend fun getSkills(): Resource<List<Skill>>

    suspend fun searchUser(query: String): Resource<List<UserItem>>

    suspend fun followUser(userId: String): SimpleResource

    suspend fun unfollowUser(userId: String): SimpleResource

    suspend fun getFollowsByUser(userId: String): Resource<List<UserItem>>

    suspend fun getFollowedToUser(userId: String): Resource<List<UserItem>>

    suspend fun getOwnProfilePicture(): Resource<String>

    fun logout()
}