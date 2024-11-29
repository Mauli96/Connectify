package com.example.connectify.feature_post.domain.use_case

import com.example.connectify.core.domain.models.UserItem
import com.example.connectify.core.util.Resource
import com.example.connectify.feature_post.domain.repository.PostRepository

class GetLikesForParentUseCase(
    private val repository: PostRepository
) {

    suspend operator fun invoke(parentId: String): Resource<List<UserItem>> {
        return repository.getLikesForParent(parentId)
    }
}