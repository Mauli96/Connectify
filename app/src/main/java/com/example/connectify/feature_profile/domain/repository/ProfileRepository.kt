package com.example.connectify.feature_profile.domain.repository

import android.net.Uri
import com.example.connectify.core.util.Resource
import com.example.connectify.core.util.SimpleResource
import com.example.connectify.feature_profile.domain.models.Profile
import com.example.connectify.feature_profile.domain.models.Skill
import com.example.connectify.feature_profile.domain.models.UpdateProfileData

interface ProfileRepository {

    suspend fun getProfile(userId: String): Resource<Profile>

    suspend fun updateProfile(
        bannerImageUri: Uri?,
        profilePictureUri: Uri?,
        updateProfileData: UpdateProfileData
    ): SimpleResource

    suspend fun getSkills(): Resource<List<Skill>>
}