package com.example.connectify.feature_profile.domain.use_case

import com.example.connectify.core.domain.models.Post
import com.example.connectify.core.util.Resource
import com.example.connectify.core.domain.repository.ProfileRepository

class GetPostsForProfileUseCase(
    private val repository: ProfileRepository
) {

    suspend operator fun invoke(
        userId: String,
        page: Int
    ): Resource<List<Post>> {
        return repository.getPostsPaged(
            userId = userId,
            page = page
        )
    }
}