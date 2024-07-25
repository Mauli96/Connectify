package com.example.connectify.feature_profile.domain.use_case

import com.example.connectify.core.domain.models.UserItem
import com.example.connectify.core.util.Resource
import com.example.connectify.core.domain.repository.ProfileRepository

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