package com.connectify.android.feature_profile.domain.use_case

import com.connectify.android.core.domain.models.UserItem
import com.connectify.android.core.domain.repository.ProfileRepository
import com.connectify.android.core.util.Resource

class GetFollowsByUserUseCase(
    private val repository: ProfileRepository
) {

    suspend operator fun invoke(userId: String): Resource<List<UserItem>> {
        return repository.getFollowsByUser(userId)
    }
}