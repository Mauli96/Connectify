package com.example.connectify.feature_profile.domain.use_case

import com.example.connectify.core.util.Resource
import com.example.connectify.feature_profile.domain.models.Profile
import com.example.connectify.core.domain.repository.ProfileRepository

class GetProfileUseCase(
    private val repository: ProfileRepository
) {

    suspend operator fun invoke(userId: String): Resource<Profile> {
        return repository.getProfile(userId)
    }
}