package com.example.connectify.core.domain.use_case

import com.example.connectify.core.util.SimpleResource
import com.example.connectify.core.domain.repository.ProfileRepository

class ToggleFollowStateForUserUseCase(
    private val repository: ProfileRepository
) {

    suspend operator fun invoke(
        userId: String,
        isFollowing: Boolean
    ): SimpleResource {
        return if(isFollowing) {
            repository.unfollowUser(userId)
        } else {
            repository.followUser(userId)
        }
    }
}