package com.connectify.android.core.domain.use_case

import com.connectify.android.core.util.SimpleResource
import com.connectify.android.core.domain.repository.ProfileRepository

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