package com.connectify.android.feature_profile.domain.use_case

import com.connectify.android.core.domain.models.UserItem
import com.connectify.android.core.util.Resource
import com.connectify.android.core.domain.repository.ProfileRepository

class SearchUserUseCase(
    private val repository: ProfileRepository
) {

    suspend operator fun invoke(query: String): Resource<List<UserItem>> {
        if(query.isBlank()) {
            return Resource.Success(data = emptyList())
        }
        return repository.searchUser(query)
    }
}