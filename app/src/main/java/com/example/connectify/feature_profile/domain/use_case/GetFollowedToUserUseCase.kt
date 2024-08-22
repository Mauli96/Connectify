package com.example.connectify.feature_profile.domain.use_case

import com.example.connectify.core.domain.models.UserItem
import com.example.connectify.core.domain.repository.ProfileRepository
import com.example.connectify.core.util.Resource

class GetFollowedToUserUseCase(
    private val repository: ProfileRepository
) {

    suspend operator fun invoke(userId: String): Resource<List<UserItem>> {
        return repository.getFollowedToUser(userId)
    }
}