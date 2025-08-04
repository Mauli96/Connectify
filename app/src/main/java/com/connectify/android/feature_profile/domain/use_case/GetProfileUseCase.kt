package com.connectify.android.feature_profile.domain.use_case

import com.connectify.android.core.util.Resource
import com.connectify.android.feature_profile.domain.models.Profile
import com.connectify.android.core.domain.repository.ProfileRepository

class GetProfileUseCase(
    private val repository: ProfileRepository
) {

    suspend operator fun invoke(userId: String): Resource<Profile> {
        return repository.getProfile(userId)
    }
}