package com.example.connectify.feature_profile.domain.use_case

import com.example.connectify.core.domain.models.Post
import com.example.connectify.core.util.Resource
import com.example.connectify.core.domain.repository.ProfileRepository
import com.example.connectify.core.util.Constants

class GetPostsForProfileUseCase(
    private val repository: ProfileRepository
) {

    suspend operator fun invoke(
        userId: String,
        page: Int,
        pageSize: Int = Constants.DEFAULT_PAGE_SIZE
    ): Resource<List<Post>> {
        return repository.getPostsPaged(
            userId = userId,
            page = page,
            pageSize = pageSize
        )
    }
}