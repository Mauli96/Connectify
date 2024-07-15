package com.example.connectify.feature_profile.domain.repository

import com.example.connectify.core.util.Resource
import com.example.connectify.feature_profile.domain.models.Profile

interface ProfileRepository {

    suspend fun getProfile(userId: String): Resource<Profile>
}