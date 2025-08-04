package com.connectify.android.feature_post.domain.use_case

import com.connectify.android.core.domain.models.UserItem
import com.connectify.android.core.util.Resource
import com.connectify.android.feature_post.domain.repository.PostRepository

class GetLikesForParentUseCase(
    private val repository: PostRepository
) {

    suspend operator fun invoke(parentId: String): Resource<List<UserItem>> {
        return repository.getLikesForParent(parentId)
    }
}